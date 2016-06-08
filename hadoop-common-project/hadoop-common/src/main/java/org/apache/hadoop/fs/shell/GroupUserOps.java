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

class GroupUserOps extends FsCommand {
  public static void registerCommands(CommandFactory factory) {
    factory.addClass(GroupUserOps.class, "-grusr");
  }
  
  public static final String NAME = "grusr";
  public static final String USAGE = "[[-a|-d|-l] <group>] [[-u] <user@ipAddr>] ...";
  public static final String DESCRIPTION =
    "Add/Delete a user from group\n" +
    "-a: add a user into group\n" +
    "-d: delete a user from group\n";    

  private boolean adduser;
  private boolean deleteuser;
	private boolean listGroup;
  private String addGroupName;
  private String deleteGroupName;
  private String usernameIp;
  
  @Override
  protected void processOptions(LinkedList<String> args) {
    CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE, "l");
    cf.addOptionWithValue("a");
    cf.addOptionWithValue("d");
    cf.addOptionWithValue("u");
    
    cf.parse(args);
    addGroupName = cf.getOptValue("a");
    deleteGroupName = cf.getOptValue("d");
    usernameIp =  cf.getOptValue("u");
    adduser = addGroupName!=null;
    listGroup = cf.getOpt("l");
    deleteuser = deleteGroupName!=null;
    out.println(adduser?"adduser":"notadduser");
    out.println(deleteuser?"deleteuser":"notdeleteuser");
    out.println(listGroup?"listGroup":"notlistGroup");
    if (args.isEmpty()) args.add(Path.CUR_DIR);
  }

  @Override
  protected void processPath(PathData item) throws IOException {
    String user = usernameIp;
    String username = user.split("@")[0];
    String userIp = user.split("@")[1];

    if(adduser){
      item.fs.addUsertoGroup(new User(username, userIp), addGroupName);
      out.println("Group:"+addGroupName);
    }
    else if(deleteuser){
      item.fs.removeUserFromGroup(new User(username, userIp), deleteGroupName);
      out.println("Group:"+deleteGroupName);
    
    }
    else{
      List<String> res = item.fs.getGroups(new User(username, userIp));
      for(String g: res){
        out.println(g);
      }
    }
  }

  @Override
  protected void processNonexistentPath(PathData item) throws IOException {
    // check if parent exists. this is complicated because getParent(a/b/c/) returns a/b/c, but
    // we want a/b
   	String user = usernameIp;
 		out.println(user);
    String username = user.split("@")[0];
    String userIp = user.split("@")[1];
    if(adduser){
      item.fs.addUsertoGroup(new User(username, userIp), addGroupName);
      out.println("Group:"+addGroupName);
    
    }
    else if(deleteuser){
      item.fs.removeUserFromGroup(new User(username, userIp), deleteGroupName);
      out.println("Group:"+deleteGroupName);
    
    }
    else{
      List<String> res = item.fs.getGroups(new User(username, userIp));
      for(String g: res){
        out.println(g);
      }
    }
  }
}
