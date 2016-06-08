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

class UserOps extends FsCommand {
  public static void registerCommands(CommandFactory factory) {
    factory.addClass(UserOps.class, "-usr");
  }
  
  public static final String NAME = "usr";
  public static final String USAGE = "[[-a|-d] <user>] ...";
  public static final String DESCRIPTION =
    "Add/Delete a user from HDFS\n";   

  private boolean adduser;
  private boolean deleteuser;
  private String userIp;
  private String addUserName;
  private String deleteUserName;
  
  @Override
  protected void processOptions(LinkedList<String> args) {
	CommandFormat cf = new CommandFormat(0, Integer.MAX_VALUE);
    cf.addOptionWithValue("a");
    cf.addOptionWithValue("d");
    cf.parse(args);
    addUserName = cf.getOptValue("a");
    deleteUserName = cf.getOptValue("d");
    adduser = cf.getOptValue("a")!=null;
    deleteuser = cf.getOptValue("d")!=null;
        

    try {
      InetAddress ipAddr = InetAddress.getLocalHost();        
      userIp = ipAddr.getHostAddress();
    } catch (UnknownHostException ex) {
        ex.printStackTrace();
    }
    if (args.isEmpty()) args.add(Path.CUR_DIR);
  }

  @Override
  protected void processPath(PathData item) throws IOException {
	  
    if(adduser){
      item.fs.createUser(new User(addUserName, userIp));
		}
    else if(deleteuser)
      item.fs.deleteUser(new User(deleteUserName, userIp));
  }

  @Override
  protected void processNonexistentPath(PathData item) throws IOException {
    // check if parent exists. this is complicated because getParent(a/b/c/) returns a/b/c, but
    // we want a/b
    if(adduser)
      item.fs.createUser(new User(addUserName, userIp));
    else if(deleteuser)
      item.fs.deleteUser(new User(deleteUserName, userIp));
  }
  
}
