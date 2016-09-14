/*
 * Copyright (c) 2016. Zuercher Hochschule fuer Angewandte Wissenschaften
 *  All Rights Reserved.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); you may
 *     not use this file except in compliance with the License. You may obtain
 *     a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *     WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *     License for the specific language governing permissions and limitations
 *     under the License.
 */

/*
 *     Author: Piyush Harsh,
 *     URL: piyush-harsh.info
 */
package ch.icclab;

import ch.cyclops.gatekeeper.GKDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

@Controller
public class GatekeeperUIController
{
    @Value("${gkdriver.conf}")
    private String gkDriverConf;

    @RequestMapping(value="/", method = RequestMethod.GET)
    public String helloWorld(Model model)
    {
        System.out.println("gkdriver-conf-file: " + gkDriverConf);
        return "login";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String indexSubmit(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        //doing authentication using gatekeeper now
        GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                model.addAttribute("authenticated", "yes");
                model.addAttribute("lastactive", System.currentTimeMillis());
                //getting the list of users
                ArrayList<String> someList = gkDriver.getUserList(0);
                model.addAttribute("usercount", someList.size());
                someList = gkDriver.getServiceList(0);
                model.addAttribute("servicecount", someList.size());
                return "index";
            }
            else
            {
                model.addAttribute("kind", "noauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            model.addAttribute("kind", "noauth");
            ex.printStackTrace();
            return "error";
        }
    }

    @RequestMapping(value="/users", method=RequestMethod.POST)
    public String listUsers(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                ArrayList<String> someList = gkDriver.getUserList(0);
                LinkedList<GKData> uList = new LinkedList<GKData>();
                for(int i=0; i<someList.size(); i++)
                {
                    String[] temp = someList.get(i).split(",");
                    GKData obj = new GKData();
                    obj.name = temp[0];
                    obj.id = temp[1];
                    uList.add(obj);
                }
                model.addAttribute("userlist", uList);
                return "userlist";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/services", method=RequestMethod.POST)
    public String listServices(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                ArrayList<String> someList = gkDriver.getServiceList(0);
                //now parse the list to separate service name and key
                LinkedList<GKData> sList = new LinkedList<GKData>();
                for(int i=0; i<someList.size(); i++)
                {
                    String[] temp = someList.get(i).split(",");
                    GKData obj = new GKData();
                    obj.name = temp[0];
                    obj.key = temp[1];
                    obj.id = temp[2];
                    sList.add(obj);
                }
                model.addAttribute("servicelist", sList);
                return "servicelist";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/registeruser", method=RequestMethod.POST)
    public String registerUForm(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                model.addAttribute("formtype", "userregistration");
                return "form";
            } else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/dogeneratetoken", method=RequestMethod.POST)
    public String dogenerateToken(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password,
                                   @RequestParam(value = "targetuid", required = true) String targetuid,
                                   @RequestParam(value = "targetpwd", required = true) String targetpwd, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);
            String msg = "";

            if(isAuth) {
                //validating inputs
                if(targetuid.trim().length() > 0)
                {
                    String token = gkDriver.generateToken(Integer.parseInt(targetuid), targetpwd);
                    if(token != null)
                    {
                        msg += "New Token: " + token;
                    }
                    else
                    {
                        msg += "Token generation failed! Check if you have used the correct user credentials.";
                    }
                }
                else
                {
                    msg += "Invalid form data. Re-enter values and try again.";
                }

                model.addAttribute("msg", msg);
                model.addAttribute("msgtype", "tokengeneration");
                return "message";
            }
            else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/doregisterservice", method=RequestMethod.POST)
    public String doregisteService(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password,
                                   @RequestParam(value = "shortname", required = true) String shortname,
                                   @RequestParam(value = "description", required = true) String description, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);
            String msg = "";

            if(isAuth) {
                //validating inputs
                if(shortname.trim().length() > 0)
                {
                    HashMap<String, String> res = gkDriver.registerService(shortname.trim(), description.trim(), 0);
                    if(res != null)
                    {
                        msg += "Service registration successful. Service URI: " + res.get("uri") + ", and unique-key: " + res.get("key");
                    }
                    else
                    {
                        msg += "Service registration failed! Check if you have admin rights to perform this action.";
                    }
                }
                else
                {
                    msg += "Invalid form data. Re-enter values and try again.";
                }

                model.addAttribute("msg", msg);
                model.addAttribute("msgtype", "serviceregistration");
                return "message";
            } else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/doregisteruser", method=RequestMethod.POST)
    public String doregisterUser(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password,
                                 @RequestParam(value = "username", required = true) String username, @RequestParam(value = "pwd1", required = true) String pwd1,
                                 @RequestParam(value = "pwd2", required = true) String pwd2, @RequestParam(value = "isadmin", required = false) boolean isadmin,
                                 @RequestParam(value = "accesslist", required = true) String accesslist, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);
            String msg = "";

            if(isAuth) {
                //validating inputs
                if(username.trim().length() > 0 && pwd1.trim().length() > 0 && pwd1.trim().startsWith(pwd2.trim()) && pwd1.trim().endsWith(pwd2.trim()))
                {
                    int user_id = gkDriver.registerUser(username.trim(), pwd1.trim(), isadmin, accesslist.trim(), 0);
                    if(user_id != -1)
                    {
                        msg += "User registration successful. Received id: " + user_id;
                    }
                    else
                    {
                        msg += "User registration failed! Check if you have admin rights to perform this action.";
                    }
                }
                else
                {
                    msg += "Invalid form data. Re-enter values and try again.";
                }

                model.addAttribute("msg", msg);
                model.addAttribute("msgtype", "userregistration");
                return "message";
            } else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/registerservice", method=RequestMethod.POST)
    public String registerSForm(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                model.addAttribute("formtype", "serviceregistration");
                return "form";
            } else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping(value="/gentoken", method=RequestMethod.POST)
    public String genTokenForm(@RequestParam(value = "userid", required = true) String userid, @RequestParam(value = "password", required = true) String password, Model model)
    {
        model.addAttribute("userid", userid);
        model.addAttribute("company", "ICCLab - Gatekeeper");
        model.addAttribute("password", password);
        try
        {
            GKDriver gkDriver = new GKDriver(gkDriverConf, Integer.parseInt(userid), password);
            boolean isAuth = gkDriver.simpleAuthentication(Integer.parseInt(userid), password);

            if(isAuth) {
                model.addAttribute("formtype", "generatetoken");
                return "form";
            } else {
                model.addAttribute("kind", "badauth");
                return "error";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping("/login")
    public String login(Model model)
    {
        return "login";
    }

    @RequestMapping(value="/error", method=RequestMethod.GET)
    public String error(Model model)
    {
        model.addAttribute("company", "ICCLab - Gatekeeper");
        return "error";
    }
}
