//
//  ViewController.swift
//  test
//
//  Created by Nathaniel Harris on 4/23/16.
//  Copyright (c) 2016 Nathaniel Harris. All rights reserved.
//

import UIKit


class LoggedInViewController: UIViewController {
    //This is the text that changes when you download your note
    @IBOutlet weak var text: UITextView!
    
    
    //This is the upload button that evenutally calls the upload functions to upload information to your notes stored online
    @IBAction func upload(sender: AnyObject) {
        var new = text.text
        //the url sent
        let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/login.php?username=default&password=password");let request = NSMutableURLRequest(URL:myUrl!);
        request.HTTPMethod = "POST";
        // Compose a query string
        let postString = "";
        request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
            data, response, error in
            //show request if there is no error
            if error != nil
            {
                println("error=\(error)")
                return
            }
            //Print out response object
            println("response = \(response)")
            // Print out response body
            let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
            println("responseString = \(responseString)")
            self.upload(responseString!, text: new)
            println("YO")
            //Convert response sent from a server side script to a NSDictionary object:
            var err: NSError?
            var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
            
            if let parseJSON = myJSON {
                //Access value of First Name by its key
                var firstNameValue = parseJSON["firstName"] as? String
                println("firstNameValue: \(firstNameValue)")
            }
            
        }
        
        task.resume()
        
        
    }
    
    //////////////////////////////////////////////////////////////
    //This is the upload function that is called by upload button to upload information to your notes stored online
    
    func upload(theKey: NSString, text: String) {
        //send the url
        if let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/note.php?key=\(theKey)&message=\(text)") {
            let request = NSMutableURLRequest(URL:myUrl);
            request.HTTPMethod = "POST";
            //OR GET
            //request.HTTPMethod = "GET";
            // Compose a query string
            let postString = "";
            request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
            println("Here")
            let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
                data, response, error in
                //show request if there is no error
                if error != nil
                {
                    println("error=\(error)")
                    return
                }
                println("Hello!")
                // You can print out response object
                println("response = \(response)")
                // Print out response body
                let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
                println("responseString = \(responseString)")
                self.label.text = (responseString! as String)
                //Convert response sent from a server side script to a NSDictionary object:
                var err: NSError?
                var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
                if let parseJSON = myJSON {
                    //Access value of First Name by its key
                    var firstNameValue = parseJSON["firstName"] as? String
                    println("firstNameValue: \(firstNameValue)")
                }
                println("Here")
                
            }
            task.resume()
            
        }
        
        
    }
    //////////////////////////////////////////////////////////////
    
    //This is the download button that evenutally calls the download functions to download information from your notes stored online
    @IBAction func download(sender: UIButton) {
        //label.text = text.text
        var new = text.text
        //send the url
        let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/login.php?username=default&password=password");let request = NSMutableURLRequest(URL:myUrl!);
        request.HTTPMethod = "POST";
        
        // Compose a query string
        let postString = "";
        
        request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
        
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
            data, response, error in
            //show request if there is no error
            if error != nil
            {
                println("error=\(error)")
                return
            }
            
            // You can print out response object
            println("response = \(response)")
            // Print out response body
            let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
            println("responseString = \(responseString)")
            self.download(responseString!, text: new)
            println("YO")
            //Convert response sent from a server side script to a NSDictionary object:
            var err: NSError?
            var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
            
            if let parseJSON = myJSON {
                // Access value of First Name by its key
                var firstNameValue = parseJSON["firstName"] as? String
                println("firstNameValue: \(firstNameValue)")
            }
            
        }
        
        task.resume()
        
    }
    
    //////////////////////////////////////////////////////////////
    
    //This is the download function that is called by download button to download information from your notes stored online
    func download(theKey: NSString, text: String) {
        //send the url
        if let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/default.note?key=\(theKey)"){
            let request = NSMutableURLRequest(URL:myUrl);
            request.HTTPMethod = "POST";
            //OR GET
            //request.HTTPMethod = "GET";
            // Compose a query string
            let postString = "";
            request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
            println("Here")
            let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
                data, response, error in
                //show request if there is no error
                if error != nil
                {
                    println("error=\(error)")
                    return
                }
                println("Hello!")
                // You can print out response object
                println("response = \(response)")
                // Print out response body
                let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
                println("responseString = \(responseString)")
                self.label.text = (responseString! as String)
                //self.sayHello(responseString!)
                //Convert response sent from a server side script to a NSDictionary object:
                var err: NSError?
                var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
                if let parseJSON = myJSON {
                    //Access value of First Name by its key
                    var firstNameValue = parseJSON["firstName"] as? String
                    println("firstNameValue: \(firstNameValue)")
                }
                
            }
            task.resume()
            
        }
        
        
    }
    //////////////////////////////////////////////////////////
    func logout(theKey: NSString) {
        //send the url
        if let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/logout.php?key=\(theKey)") {
            
            let request = NSMutableURLRequest(URL:myUrl);
            request.HTTPMethod = "POST";
            //OR GET
            //request.HTTPMethod = "GET";
            // Compose a query string
            let postString = "";
            request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
            let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
                data, response, error in
                //show request if there is no error
                if error != nil
                {
                    println("error=\(error)")
                    return
                }
                // You can print out response object
                println("response = \(response)")
                // Print out response body
                let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
                println("responseString = \(responseString)")
                self.label.text = (responseString! as String)
                //Convert response sent from a server side script to a NSDictionary object:
                var err: NSError?
                var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
                if let parseJSON = myJSON {
                    //Access value of First Name by its key
                    var firstNameValue = parseJSON["firstName"] as? String
                    println("firstNameValue: \(firstNameValue)")
                }
                
            }
            task.resume()
            
        }
        
        
    }

    //////////////////////////////////////////////////////////////
    @IBOutlet var label: UILabel!
    //////////////////////////////////////////////////////////////
    
    @IBAction func logOut(sender: AnyObject) {
        var new = text.text
        //send the url
        let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/login.php?username=default&password=password");let request = NSMutableURLRequest(URL:myUrl!);
        request.HTTPMethod = "POST";
        
        // Compose a query string
        let postString = "";
        
        request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
        
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
            data, response, error in
            //show request if there is no error
            if error != nil
            {
                println("error=\(error)")
                return
            }
            
            // You can print out response object
            println("response = \(response)")
            // Print out response body
            let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
            println("responseString = \(responseString)")
            self.logout(responseString!)
            println("YO")
            //Convert response sent from a server side script to a NSDictionary object:
            var err: NSError?
            var myJSON = NSJSONSerialization.JSONObjectWithData(data, options: .MutableLeaves, error:&err) as? NSDictionary
            
            if let parseJSON = myJSON {
                //Access value of First Name by its key
                var firstNameValue = parseJSON["firstName"] as? String
                println("firstNameValue: \(firstNameValue)")
            }
            
        }
        
        task.resume()
    }
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    //the done button that has the keyboard dissappear
    @IBAction func done(sender: UIButton) {
        dismissKeyboard()
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}