
//
//  ViewController.swift
//  test
//
//  Created by Nathaniel Harris on 4/23/16.
//  Copyright (c) 2016 Nathaniel Harris. All rights reserved.
//

import UIKit

class LoggedInViewController: UIViewController {
    
    @IBOutlet weak var text: UITextView!
    
    @IBAction func upload(sender: AnyObject) {
        let syncText = text.text;
        
    }

    @IBAction func download(sender: AnyObject) {
        text.text = ""
        let syncText = text.text;
        
    }
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func done(sender: UIButton) {
    dismissKeyboard()
    }


    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        let myUrl = NSURL(string: "http://www.tcnj.edu/~ottj3/notes.php");
        let request = NSMutableURLRequest(URL:myUrl!);
        request.HTTPMethod = "POST";
        
        // Compose a query string
        let postString = "?username=default&password=password";
        
        request.HTTPBody = postString.dataUsingEncoding(NSUTF8StringEncoding);
        
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
            data, response, error in
            
            if error != nil{
                println(request)
                return
            }
            
            // You can print out response object
            println("response = \(response)")
            
            // Print out response body
            let responseString = NSString(data: data, encoding: NSUTF8StringEncoding)
            println("responseString = \(responseString)")
            
            //Let’s convert response sent from a server side script to a NSDictionary object:
        
            
        }
        
        task.resume()

        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}


