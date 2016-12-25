//
//  ViewController.swift
//  playfield-locator
//
//  Created by chris vugrinec on 24-12-16.
//  Copyright © 2016 datalinks. All rights reserved.
//

import UIKit
import AVFoundation
import CoreLocation



class ViewController: UIViewController, CLLocationManagerDelegate {

    let locManager = CLLocationManager()
    var backgroundTask: UIBackgroundTaskIdentifier = UIBackgroundTaskInvalid
    var updateTimer: Timer?
    @IBOutlet weak var useridField: UITextField!
    
    @IBAction func buttonPressedWithSenderWithSender(_ sender: UIButton) {
        print("You clicked the button!!!!! "+useridField.text!)
        UserDefaults.standard.set(useridField.text, forKey: "USER")
        doRestCall()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if(UserDefaults.standard.object(forKey: "USER") != nil){
            useridField.text = UserDefaults.standard.object(forKey: "USER") as? String
        }else {
            useridField.placeholder = "username"
        }
        
        locManager.delegate = self
        if(CLLocationManager.authorizationStatus() != CLAuthorizationStatus.authorizedWhenInUse){
            locManager.requestWhenInUseAuthorization()
        }
        
        //  Background job
        NotificationCenter.default.addObserver(self, selector: #selector(reinstateBackgroundTask), name: NSNotification.Name.UIApplicationDidBecomeActive, object: nil)
        registerBackgroundTask()
        
        Timer.scheduledTimer(timeInterval: 3.0, target: self,selector: #selector(test), userInfo: nil, repeats: true)
    }

    
    func doRestCallPOST(){
        
        //let firstTodoEndpoint: String = "http://httpbin.org/ip"
        var request = URLRequest(url: URL(string: "http://httpbin.org/ip")!)
        request.httpMethod = "POST"
        let session = URLSession.shared
        
        session.dataTask(with: request) {data, response, err in
            print("Entered the completionHandler")
        }.resume()
    }
    
    func doRestCall(){
//        let parseData = parseJSON(getJSON("https://httpbin.org/ip"))
        let config = URLSessionConfiguration.default // Session Configuration
        let session = URLSession(configuration: config) // Load configuration into Session
        let url = URL(string: "https://httpbin.org/ip")!
        
        let task = session.dataTask(with: url, completionHandler: {
            (data, response, error) in
            
            if error != nil {
                
                print(error!.localizedDescription)
                
            } else {
                
                do {
                    
                    if let json = try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? [String: Any]
                    {
                        
                        //Implement your logic
                        print(json)
                        
                    }
                    
                } catch {
                    
                    print("error in JSONSerialization")
                    
                }
                
                
            }
            
        })
        task.resume()
        
    }
    
    
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    func reinstateBackgroundTask() {
        if updateTimer != nil && (backgroundTask == UIBackgroundTaskInvalid) {
            registerBackgroundTask()
        }
    }
    
    func registerBackgroundTask() {
        backgroundTask = UIApplication.shared.beginBackgroundTask { [weak self] in
            self?.endBackgroundTask()
        }
        test()
        assert(backgroundTask != UIBackgroundTaskInvalid)
    }
    

    
    func endBackgroundTask() {
        print("Background task ended.")
        UIApplication.shared.endBackgroundTask(backgroundTask)
        backgroundTask = UIBackgroundTaskInvalid
    }
    
    func test(){
            switch UIApplication.shared.applicationState {
            case .active:
                print("hello active world")
            case .background:
                print("hello background world")
            case .inactive:
                print("hello inactive world")
                break
            
            }

    }
    
    func startScanning() {
        let uuid = UUID(uuidString: "E2C56DB5-DFFB-48D2-B060-D0F5A71096E0")!
        let region = CLBeaconRegion(proximityUUID: uuid, identifier: "pflocator-")

        locManager.startMonitoring(for: region)
        locManager.startRangingBeacons(in: region)
        print("start scanning")
    }

  
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
            if CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self) {
                if CLLocationManager.isRangingAvailable() {
                    startScanning()
                }
            }
    }
    
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    func playSound(soundNumber: UInt32){
        
        AudioServicesPlaySystemSound (soundNumber)
        sleep(5)
        
    }
    
    

    func updateDistance(_ distance: CLProximity) {
        UIView.animate(withDuration: 0.8) {
            switch distance {
            case .unknown:
                self.view.backgroundColor = UIColor.gray
                
            case .far:
                self.view.backgroundColor = UIColor.blue
                
            case .near:
                self.view.backgroundColor = UIColor.orange
                
            case .immediate:
                self.view.backgroundColor = UIColor.red
            }
        }
    }
    
    
    func locationManager(_ manager: CLLocationManager, didRangeBeacons beacons: [CLBeacon], in region: CLBeaconRegion) {
        //let knownBeacons = beacons.filter{ $0.proximity == CLProximity.immediate }
        //  knownbeacons from database per club
        let dbBeacons: [NSNumber] = [ 13382 ];
        let knownBeacons = beacons.filter{ dbBeacons.contains($0.major) }
        
        
        for beacon in knownBeacons {
            updateDistance(beacon.proximity)
            print("pflocator-" + String(describing: beacon.major)+" strength: "+String(describing: beacon.rssi )+" proximity: "+String(describing: beacon.proximity.rawValue) )
            
            /*
            if(beacon.major==13382){
                playSound(soundNumber: 1005)
            }
            if(beacon.major==13424){
                playSound(soundNumber: 1000)
            }
            */
        }

    }
    
    

}

