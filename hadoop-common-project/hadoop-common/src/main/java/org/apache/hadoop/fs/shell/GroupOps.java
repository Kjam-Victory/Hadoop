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

package org.apache.hadoop.fs.shell;

import org.apache.hadoop.database.*;
import java.util.List;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.hadoop.net.NetUtils;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.PathIsNotDirectoryException;
import org.apache.hadoop.fs.PathNotFoundException;

/**
 * Create the given dir
 */
@InterfaceAudience.Private
@InterfaceStability.Unstable

class GroupOps extends FsCommand {
  public static void registerCommands(CommandFactory factory) {
    factory.addClass(GroupOps.class, "-group");
  }
  
  public static final String NAME = "group";
  public static final String USAGE = "[[-c|-d|-l] <group>] ...";
  public static final String DESCRIPTION =
    "Create/Delete/List a Group\n" +
    "-c: create a group according to the groupname given\n" +
    "-d: delete a group according to the groupname given\n" +
    "-l: list all the groups\n";

  private boolean createGroup;
  private boolean deleteGroup;
  private boolean listGroup;
  private String createGroupName;
  private String deleteGroupName;
  
  @Override
  protected void processOptions(LinkedList<String> args) {
	CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, "l");
    cf.addOptionWithValue("c");
    cf.addOptionWithValue("d");
    cf.parse(args);    
    createGroupName = cf.getOptValue("c");
    deleteGroupName = cf.getOptValue("d");
    createGroup = createGroupName!=null;
    deleteGroup = deleteGroupName!=null;
    listGroup = cf.getOpt("l");
    if (args.isEmpty()) args.add(Path.CUR_DIR);
  }

  @Override
  protected void processPath(PathData item) throws IOException {
    // if (item.stat.isDirectory()) {      
    //   throw new PathExistsException(item.toString());
      
    // } else {
    //   throw new PathIsNotDirectoryException(item.toString());
    // }
    String userIp = NetUtils.getRealLocalHost();
    String username = System.getProperty("user.name");

    if(createGroup)
      item.fs.createGroup(createGroupName, new User(username, userIp));
    else if(deleteGroup)
      item.fs.deleteGroup(deleteGroupName);
    else if(listGroup){
      List<String> groups = item.fs.getAllGroups();
      out.println("HDFS Groups:\n");
      for(String group: groups){
        out.println("Group: "+group);  
      }      
    }
  }

  @Override
  protected void processNonexistentPath(PathData item) throws IOException {
    // check if parent exists. this is complicated because getParent(a/b/c/) returns a/b/c, but
    // we want a/b
    String userIp = NetUtils.getRealLocalHost();
    String username = System.getProperty("user.name");
    if(createGroup)
      item.fs.createGroup(createGroupName, new User(username, userIp));
    else if(deleteGroup)
      item.fs.deleteGroup(deleteGroupName);
    else if(listGroup){
      List<String> groups = item.fs.getAllGroups();
      out.println("HDFS Groups:\n");
      for(String group: groups){
        out.println("Group: "+group);  
      }      
    }
  }
}
