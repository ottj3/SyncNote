//
//  ViewController.swift
//  SyncNote
//
//  Created by Randell Carrido on 4/20/16.
//  Copyright (c) 2016 Randell Carrido. All rights reserved.
//

import UIKit

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //When you click done then the keyboard goes away
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)
    }
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    //Below are all the buttons and text fields for the login page
    //The blue backround image
    @IBOutlet weak var BackgroundBlue: UIImageView!
    //The username text field
    @IBOutlet weak var username: UITextField!
    //The password text field
    @IBOutlet weak var password: UITextField!
    //The login button
    @IBAction func LoginRegister(sender: AnyObject) {
        //set variables equal to the text entered by the user
        let user = username.text;
        let pass = password.text;
        //Display an error message if the user does not enter a username or password
        if(user.isEmpty || pass.isEmpty){
            //Alerts when missing username or password
            displayMyAlertMessage("Please type in both your username and password");
            return;
        }
////////////////////////////////////////////////////////////////////////////
        //if(user != nil || pass != nil){
        //the url sent is the following url plus the entered information from the user
        let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/login.php?username="+user+"&password="+pass);
        let request = NSMutableURLRequest(URL:myUrl!);
        //Do a post method to get a key for login
        request.HTTPMethod = "POST";
        
        // Compose a query string
        let postString = "?username="+user+"&password="+pass;
        request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
            data, response, error in
            //show request if there is no error
            if error != nil{
                println(request)
                return
            }
            
            // You can print out response object
            println("response = \(response)")
            
            // Print out response body
            let responseString = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("responseString = \(responseString)")
            let rs = responseString!
            println(rs);
            if(rs as String == ""){
                //self.displayMyAlertMessage("Please type in both your username and password");
                self.dismissViewControllerAnimated(false, completion: nil);
            }

        }
            task.resume()

       
    }
    
//The function that display the error message if the user does not input any information
    func displayMyAlertMessage(userMessage:String)
    {
        
        var myAlert = UIAlertController(title:"Alert", message:userMessage, preferredStyle: UIAlertControllerStyle.Alert);
        
        let okAction = UIAlertAction(title:"Ok", style:UIAlertActionStyle.Default, handler:nil);
        
        myAlert.addAction(okAction);
        
        self.presentViewController(myAlert, animated:true, completion:nil);
        
    }
}

