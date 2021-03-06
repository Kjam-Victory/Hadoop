/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.security;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.hadoop.database.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.util.Shell;
import org.apache.hadoop.util.Shell.ExitCodeException;
import org.apache.hadoop.util.Shell.ShellCommandExecutor;

/**
 * A simple shell-based implementation of {@link GroupMappingServiceProvider} 
 * that exec's the <code>groups</code> shell command to fetch the group
 * memberships of a given user.
 */
@InterfaceAudience.LimitedPrivate({"HDFS", "MapReduce"})
@InterfaceStability.Evolving
public class HadoopGroupMapping
  implements GroupMappingServiceProvider {
  
  private static final Log LOG =
    LogFactory.getLog(HadoopGroupMapping.class);

  @SuppressWarnings("serial")
  private static class PartialGroupNameException extends IOException {
    public PartialGroupNameException(String message) {
      super(message);
    }

    public PartialGroupNameException(String message, Throwable err) {
      super(message, err);
    }

    @Override
    public String toString() {
      final StringBuilder sb =
          new StringBuilder("PartialGroupNameException ");
      sb.append(super.getMessage());
      return sb.toString();
    }
  }
  /**
   * Returns list of groups for a user
   *
   * @param userName get groups for this user
   * @return list of groups for a given user
   */
  @Override
  public List<String> getGroups(String userName) throws IOException {
	  List<String> groups = null;
	  try {
		  DBHelper DBhelper = new DBHelper();
          InetAddress ipAddr = InetAddress.getLocalHost();
          String userIpAddr = userName.split("/")[1];
          DBHelper dbHelper = new DBHelper();
          String realUserName = userName.split("/")[0];
          org.apache.hadoop.database.User user = new org.apache.hadoop.database.User(realUserName, userIpAddr);
					groups = DBhelper.getGroups(user);
          System.out.println(userIpAddr);
      } catch (UnknownHostException ex) {
          ex.printStackTrace();
          System.out.println("Group Name Mapping Service Failed!");
      }
	  
	  return groups;
  }

  /**
   * Caches groups, no need to do that for this provider
   */
  @Override
  public void cacheGroupsRefresh() throws IOException {
    // does nothing in this provider of user to groups mapping
  }

  /** 
   * Adds groups to cache, no need to do that for this provider
   *
   * @param groups unused
   */
  @Override
  public void cacheGroupsAdd(List<String> groups) throws IOException {
    // does nothing in this provider of user to groups mapping
  }

  /**
   * Create a ShellCommandExecutor object using the user's name.
   *
   * @param userName user's name
   * @return a ShellCommandExecutor object
   */
  protected ShellCommandExecutor createGroupExecutor(String userName) {
    return new ShellCommandExecutor(
        Shell.getGroupsForUserCommand(userName), null, null, 0L);
  }

  /**
   * Create a ShellCommandExecutor object for fetch a user's group id list.
   *
   * @param userName the user's name
   * @return a ShellCommandExecutor object
   */
  protected ShellCommandExecutor createGroupIDExecutor(String userName) {
    return new ShellCommandExecutor(
        Shell.getGroupsIDForUserCommand(userName), null, null, 0L);
  }

  /**
   * Get the current user's group list from Unix by running the command 'groups'
   * NOTE. For non-existing user it will return EMPTY list.
   *
   * @param user get groups for this user
   * @return the groups list that the <code>user</code> belongs to. The primary
   *         group is returned first.
   * @throws IOException if encounter any error when running the command
   */

  /**
   * Attempt to parse group names given that some names are not resolvable.
   * Use the group id list to identify those that are not resolved.
   *
   * @param groupNames a string representing a list of group names
   * @param groupIDs a string representing a list of group ids
   * @return a linked list of group names
   * @throws PartialGroupNameException
   */
  private List<String> parsePartialGroupNames(String groupNames,
      String groupIDs) throws PartialGroupNameException {
    StringTokenizer nameTokenizer =
        new StringTokenizer(groupNames, Shell.TOKEN_SEPARATOR_REGEX);
    StringTokenizer idTokenizer =
        new StringTokenizer(groupIDs, Shell.TOKEN_SEPARATOR_REGEX);
    List<String> groups = new LinkedList<String>();
    while (nameTokenizer.hasMoreTokens()) {
      // check for unresolvable group names.
      if (!idTokenizer.hasMoreTokens()) {
        throw new PartialGroupNameException("Number of group names and ids do"
        + " not match. group name =" + groupNames + ", group id = " + groupIDs);
      }
      String groupName = nameTokenizer.nextToken();
      String groupID = idTokenizer.nextToken();
      if (!StringUtils.isNumeric(groupName) ||
          !groupName.equals(groupID)) {
        // if the group name is non-numeric, it is resolved.
        // if the group name is numeric, but is not the same as group id,
        // regard it as a group name.
        // if unfortunately, some group names are not resolvable, and
        // the group name is the same as the group id, regard it as not
        // resolved.
        groups.add(groupName);
      }
    }
    return groups;
  }

  /**
   * Attempt to partially resolve group names.
   *
   * @param userName the user's name
   * @param errMessage error message from the shell command
   * @param groupNames the incomplete list of group names
   * @return a list of resolved group names
   * @throws PartialGroupNameException
   */
  private List<String> resolvePartialGroupNames(String userName,
      String errMessage, String groupNames) throws PartialGroupNameException {
    // Exception may indicate that some group names are not resolvable.
    // Shell-based implementation should tolerate unresolvable groups names,
    // and return resolvable ones, similar to what JNI-based implementation
    // does.
    if (Shell.WINDOWS) {
      throw new PartialGroupNameException("Does not support partial group"
      + " name resolution on Windows. " + errMessage);
    }
    if (groupNames.isEmpty()) {
      throw new PartialGroupNameException("The user name '" + userName
          + "' is not found. " + errMessage);
    } else {
      LOG.warn("Some group names for '" + userName + "' are not resolvable. "
          + errMessage);
      // attempt to partially resolve group names
      try {
        ShellCommandExecutor exec2 = createGroupIDExecutor(userName);
        exec2.execute();
        return parsePartialGroupNames(groupNames, exec2.getOutput());
      } catch (ExitCodeException ece) {
        // If exception is thrown trying to get group id list,
        // something is terribly wrong, so give up.
        throw new PartialGroupNameException("failed to get group id list for " +
        "user '" + userName + "'", ece);
      } catch (IOException ioe) {
        throw new PartialGroupNameException("can't execute the shell command to"
        + " get the list of group id for user '" + userName + "'", ioe);
      }
    }
  }

  /**
   * Split group names into a linked list.
   *
   * @param groupNames a string representing the user's group names
   * @return a linked list of group names
   */
  private List<String> resolveFullGroupNames(String groupNames) {
    StringTokenizer tokenizer =
        new StringTokenizer(groupNames, Shell.TOKEN_SEPARATOR_REGEX);
    List<String> groups = new LinkedList<String>();
    while (tokenizer.hasMoreTokens()) {
      groups.add(tokenizer.nextToken());
    }

    return groups;
  }
}
