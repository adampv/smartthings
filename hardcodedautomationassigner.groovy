/**
 *  Copyright 2015 AdamV
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Virtual Button assigner to iTach
 *
 *	Author: AdamV
 *	Date: 2015-10-13
 */
definition(
    name: "Hard Coded Automation assigner",
    namespace: "AdamV",
    author: "AdamV",
    description: "Assign automations of buttons to iTach Commands",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MyApps/Cat-MyApps@2x.png"
)

preferences {
	page(name: "selectController")
	page(name: "configureButton1")
	page(name: "configureButton2")
	page(name: "configureButton3")
	page(name: "configureButton4")

}

def selectController() {
	dynamicPage(name: "selectController", title: "First, select your button device", nextPage: "configureButton1", uninstall: configured()) {
		section {
			input "buttonDevice", "capability.button", title: "Controller", multiple: false, required: true
            input "theSwitch", "capability.switch", title: "EntSwitch", multiple: false, required: true
            input "powerMeter", "capability.powerMeter", title: "powerMeter", multiple: false, required: true
            input "projSwitch", "capability.switch", title: "projSwitch", multiple: false, required: true
            input "speaker1", "capability.switch", title: "speaker1", multiple: false, required: true
            input "speaker2", "capability.switch", title: "speaker2", multiple: false, required: true
            input "speaker3", "capability.switch", title: "speaker3", multiple: false, required: true
            input "speaker4", "capability.switch", title: "speaker4", multiple: false, required: true
            input "speaker5", "capability.switch", title: "speaker5", multiple: false, required: false
            input "speaker6", "capability.switch", title: "speaker6", multiple: false, required: false
            input "blind1", "capability.switch", title: "blind1", multiple: false, required: false
            input "blind2", "capability.switch", title: "blind2", multiple: false, required: false
            input "blind3", "capability.switch", title: "blind3", multiple: false, required: false
		}

	}
}

def configureButton1() {
	dynamicPage(name: "configureButton1", title: "Now let's decide how to use the first button",
		nextPage: "configureButton2", uninstall: configured(), getButtonSections(1))
}
def configureButton2() {
	dynamicPage(name: "configureButton2", title: "If you have a second button, set it up here",
		nextPage: "configureButton3", uninstall: configured(), getButtonSections(2))
}

def configureButton3() {
	dynamicPage(name: "configureButton3", title: "If you have a third button, you can do even more here",
		nextPage: "configureButton4", uninstall: configured(), getButtonSections(3))
}
def configureButton4() {
	dynamicPage(name: "configureButton4", title: "If you have a fourth button, you rule, and can set it up here",
		install: true, uninstall: true, getButtonSections(4))
}


def getButtonSections(buttonNumber) {
	return {
		section("Denon AVR X2000") {
        		input "function", "enum", title: "Pick a function", multiple: true, required: false,
					options: ["On/Off", "VolUp", "VolDown", "Up", "Down", "Left", "Right", "Ok", "Menu", "Mute"]
		}
/*		section("Locks") {
			input "locks_${buttonNumber}_pushed", "capability.lock", title: "Pushed", multiple: true, required: false
			input "locks_${buttonNumber}_held", "capability.lock", title: "Held", multiple: true, required: false
		}
		section("Sonos") {
			input "sonos_${buttonNumber}_pushed", "capability.musicPlayer", title: "Pushed", multiple: true, required: false
			input "sonos_${buttonNumber}_held", "capability.musicPlayer", title: "Held", multiple: true, required: false
		}
		section("Modes") {
			input "mode_${buttonNumber}_pushed", "mode", title: "Pushed", required: false
			input "mode_${buttonNumber}_held", "mode", title: "Held", required: false
		}
		def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
			section("Hello Home Actions") {
				log.trace phrases
				input "phrase_${buttonNumber}_pushed", "enum", title: "Pushed", required: false, options: phrases
				input "phrase_${buttonNumber}_held", "enum", title: "Held", required: false, options: phrases
			}
		}
        section("Sirens") {
            input "sirens_${buttonNumber}_pushed","capability.alarm" ,title: "Pushed", multiple: true, required: false
            input "sirens_${buttonNumber}_held", "capability.alarm", title: "Held", multiple: true, required: false
        }

		section("Custom Message") {
			input "textMessage_${buttonNumber}", "text", title: "Message", required: false
		}

        section("Push Notifications") {
            input "notifications_${buttonNumber}_pushed","bool" ,title: "Pushed", required: false, defaultValue: false
            input "notifications_${buttonNumber}_held", "bool", title: "Held", required: false, defaultValue: false
        }

        section("Sms Notifications") {
            input "phone_${buttonNumber}_pushed","phone" ,title: "Pushed", required: false
            input "phone_${buttonNumber}_held", "phone", title: "Held", required: false
        }
        */
	}
}

def installed() {
	initialize()
}

def updated() {
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(buttonDevice, "button", buttonEvent)
}

def configured() {
	return buttonDevice || buttonConfigured(1) || buttonConfigured(2) || buttonConfigured(3) || buttonConfigured(4)
}

def buttonConfigured(idx) {
	return settings["lights_$idx_pushed"] ||
		settings["locks_$idx_pushed"] ||
		settings["sonos_$idx_pushed"] ||
		settings["mode_$idx_pushed"] ||
        settings["notifications_$idx_pushed"] ||
        settings["sirens_$idx_pushed"] ||
        settings["notifications_$idx_pushed"]   ||
        settings["phone_$idx_pushed"]
}

def buttonEvent(evt){
	if(allOk) {
    	log.debug(evt.data)
        String [] sections = evt.data.split( ":" )
	    log.debug("sections: $sections")

       	String payload = sections[ 1 ]
		log.debug( "Command: $payload" )
        
		String payload2 = payload.replaceAll("[/}/g]","")
        Integer payload3 = payload2.toInteger()
        
		def buttonNumber = payload3 // why doesn't jsonData work? always returning [:]
		def value = evt.value
		log.debug "buttonEvent: $evt.name = $evt.value ($evt.data)"
		log.debug "button: $buttonNumber, value: $value"

//		def recentEvents = 0
        
        //buttonDevice.eventsSince(new Date(now() - 3000)).findAll{it.value == evt.value && it.data == evt.data}
		//log.debug "Found ${recentEvents.size()?:0} events in past 3 seconds"

		executeHandlers(buttonNumber, value)
//		if(recentEvents.size <= 1){
		/*	switch(buttonNumber) {
				case 1:
					executeHandlers(1, value)
					break
				case 2:
					executeHandlers(2, value)
					break
				case 3:
					executeHandlers(3, value)
					break
				case 4:
					executeHandlers(4, value)
					break
           		case 5:
					executeHandlers(5, value)
					break
           		case 6:
					executeHandlers(6, value)
					break
                case 7:
					executeHandlers(7, value)
					break
                case 8:
					executeHandlers(8, value)
					break
				case 9:
					executeHandlers(9, value)
					break
           		case 10:
					executeHandlers(10, value)
					break
           		case 11:
					executeHandlers(11, value)
					break
                case 12:
					executeHandlers(12, value)
					break
			} */
	//	} 
    //    else {
	//		log.debug "Found recent button press events for $buttonNumber with value $value"
	//	}
	}
}

/*                   	 def denonon() {
						sendHubCommand(new physicalgraph.device.HubAction("""sendir,1:3,1,38000,1,1,11,29,10,70,10,30,10,30,10,30,10,70,10,30,10,30,10,30,10,30,10,30,10,70,10,70,10,30,10,30,10,1776,10,30,10,70,10,30,10,30,10,30,10,30,10,70,10,70,10,70,10,70,10,70,10,30,10,30,10,70,10,70,10,1617,10,70,10,30,10,30,10,30,10,70,10,30,10,30,10,30,10,30,10,30,10,70,10,70,10,30,10,30,10,1776,10,30,10,70,10,30,10,30,10,30,10,30,10,70,10,70,10,70,10,70,10,70,10,30,10,30,10,70,10,70,10,1617\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
   						 }

					def denonblu() {
						sendHubCommand(new physicalgraph.device.HubAction("""sendir,1:3,6,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2841,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2842,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,3800\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}")) 
						}
*/                    
def executeHandlers(buttonNumber, value) {
	// log.debug "executeHandlers: $buttonNumber - $value"
    	def deviceNetworkId = "C0A8019B:1386"
        // def deviceNetworkId = "C0A80149:1386"
        def theCom1
        def theCom2
        def theCom3
        def theCom4
        def theCom5
                    if (value == "pushed" && buttonNumber == 1) {
                    	log.debug "running sequence: Watch Disc"
                        
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        
                        if (currentSwitch == "off" && currentProj == "off" && currentSpeaker == "off"){                   
							runIn(1, EntOn)
                        	runIn(1, ProjDown)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(18, ProOn)
                        	runIn(20, DenBlu)
                        	runIn(22, BluOn)     
                        	}
                       	else if (currentSwitch == "off" && currentProj == "on"){                   
							runIn(1, EntOn)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(20, DenBlu)
                        	runIn(22, BluOn)     
                        	}
                        else if (currentSwitch == "on" && currentProj == "off"){                   
                        	runIn(1, ProjDown)
                            runIn(2, BlindDown)
                        	runIn(3, ProOn)
                        	runIn(4, DenBlu)
                        	runIn(5, BluOn)  
                        	}
                        else if (currentSwitch == "on" && currentProj == "on"){                   
                        	runIn(1, BlindDown)
                            runIn(4, DenBlu)
                        	runIn(5, BluOn)  
                        	}
                        }
                        if (value == "pushed" && buttonNumber == 7) {
                    	log.debug "running sequence: Watch TV"
                        
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        
                        if (currentSwitch == "off" && currentProj == "off" && currentSpeaker == "off"){                   
							runIn(1, EntOn)
                        	runIn(1, ProjDown)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(18, ProOn)
                        	runIn(20, DenBlu)
                        	runIn(22, BluOn)
                            runIn(48, BluTV)
                        	}
                       	else if (currentSwitch == "off" && currentProj == "on"){                   
							runIn(1, EntOn)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(20, DenBlu)
                        	runIn(22, BluOn)
                            runIn(48, BluTV)
                        	}
                        else if (currentSwitch == "on" && currentProj == "off"){                   
                        	runIn(1, ProjDown)
                            runIn(2, BlindDown)
                        	runIn(3, ProOn)
                        	runIn(4, DenBlu)
                        	runIn(5, BluOn)
                            runIn(6, BluTV)
                        	}
                        else if (currentSwitch == "on" && currentProj == "on"){                   
                        	runIn(1, DenBlu)
                        	runIn(2, BlindDown)
                            runIn(3, BluOn)
                            runIn(10, BluTV)
                        	}
                        }
                    /*if (value == "pushed" && buttonNumber == 7) { //Old automation
               			log.debug "running sequence: Watch TV"
						runIn(1, EntOn)
                        runIn(1, ProjDown)
                        runIn(2, speakersOn)
                        runIn(14, DenOn)
                        runIn(18, ProOn)
                        runIn(20, DenBlu)
                        runIn(22, BluOn)
                        runIn(48, BluTV)
                        }*/
                        if (value == "pushed" && buttonNumber == 5) {
                    	log.debug "running sequence: Watch Nexus"
                        
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        
                        if (currentSwitch == "off" && currentProj == "off" && currentSpeaker == "off"){                   
							runIn(1, EntOn)
                        	runIn(1, ProjDown)
                            runIn(3, BlindDown)
                        	runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(19, ProOn)
                        	runIn(21, DenNex)
                        	}
                       	else if (currentSwitch == "off" && currentProj == "on"){                   
							runIn(1, EntOn)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(20, DenNex)
                        	}
                        else if (currentSwitch == "on" && currentProj == "off"){                   
                        	runIn(1, ProjDown)
                            runIn(2, BlindDown)
                        	runIn(3, ProOn)
                        	runIn(4, DenNex)
                        	}
                        else if (currentSwitch == "on" && currentProj == "on"){                   
                        	runIn(1, DenNex)
                            runIn(2, BlindDown)
                        	}
                        }    
                 /*   if (value == "pushed" && buttonNumber == 5) { //old automation
               			log.debug "running sequence: Watch Nexus"
						runIn(1, EntOn)
                        runIn(1, ProjDown)
                        runIn(2, speakersOn)
                        runIn(14, DenOn)
                        runIn(18, ProOn)
                        runIn(20, DenNex)
                        } */   
                    if (value == "pushed" && buttonNumber == 2) {
                    	log.debug "running sequence: Watch Apple TV"
                        
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        
                        if (currentSwitch == "off" && currentProj == "off" && currentSpeaker == "off"){                   
							runIn(1, EntOn)
                        	runIn(1, ProjDown)
                            runIn(3, BlindDown)
                        	runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(18, ProOn)
                        	runIn(20, DenApp)
                        	}
                       	else if (currentSwitch == "off" && currentProj == "on"){                   
							runIn(1, EntOn)
                            runIn(3, BlindDown)
                            runIn(2, speakersOn)
                        	runIn(14, DenOn)
                        	runIn(20, DenApp)
                        	}
                        else if (currentSwitch == "on" && currentProj == "off"){                   
                        	runIn(1, ProjDown)
                            runIn(2, BlindDown)
                        	runIn(3, ProOn)
                        	runIn(4, DenApp)
                        	}
                        else if (currentSwitch == "on" && currentProj == "on"){                   
                        	runIn(2, BlindDown)
                            runIn(1, DenApp)
                        	}
                        }
                 /*   if (value == "pushed" && buttonNumber == 2) {
               			log.debug "running sequence: Watch AppleTV"
						runIn(1, EntOn)
                        runIn(1, ProjDown)
                        runIn(2, speakersOn)
                        runIn(14, DenOn)
                        runIn(18, ProOn)
                        runIn(20, DenApp)
                        } */ //Old Automation
                        if (value == "pushed" && buttonNumber == 8) {
                    	log.debug "running sequence: Listen to music"
                        
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        def currentPower = currentSwitchPower()
                        
                        if (currentSwitch == "off"){                   
							runIn(1, EntOn)
                        	runIn(14, DenOn)
                        	runIn(18, DenNex)
                        	}
                        if (currentPower > 103){                   
                        	runIn(4, BluOn)
                        	}
                        if (currentSwitch == "on"){                   
                        	runIn(1, DenNex)
                        	}
						if (currentProj == "on"){                   
                            runIn(3, ProOff)
                        	runIn(4, ProjUp)
                        	}
                        if (currentSpeaker == "off"){                   
                        	runIn(2, speakersOn)
                        	}
                        }
                  /*  if (value == "pushed" && buttonNumber == 8) {
               			log.debug "running sequence: Listen to music"
						runIn(1, EntOn)
                        runIn(2, speakersOn)
                        runIn(14, DenOn)
                        runIn(18, DenNex)
                        }  */ // Old Automation
                        
					if (value == "pushed" && buttonNumber == 9) {
                     	log.debug "running sequence: Turn off Everything"
                        runIn(1, BlindUp)
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        def currentPower = currentSwitchPower()
                        log.debug("current power = $currentPower")
                        
                        if (currentSwitch == "on"){                   
                        	
                            if (currentPower > 50){                   
                        	runIn(5, DenOn)
                        	}
                        //	runIn(5, BluOn)
                            runIn(500, EntOff)
                        	}
                        if (currentPower > 103){                   
                        	runIn(4, BluOn)
                        	}
                        if (currentProj == "on"){                   
                            runIn(1, ProOff)
                        	runIn(2, ProjUp)
                            runIn(3, BlindUp)
                        	}
                        if (currentSpeaker == "on"){                   
                        	runIn(6, speakersOff)
                        	}
                        }
                            
					/*	runIn(1, ProOff)
                        runIn(2, ProjUp)
                        runIn(3, DenOn)
                        runIn(5, BluOn)
                        runIn(10, speakersOff)
                        runIn(25, EntOff)
                        }  */ //Old automation  
              		if (value == "pushed" && buttonNumber == 11) {
                     	log.debug "running sequence: Turn off Music"
						runIn(1, DenOn)
                        runIn(1, ProjUp)
                        runIn(5, speakersOff)
                        runIn(6, EntOff)
                        } 
                    if (value == "pushed" && buttonNumber == 12) {
                     	log.debug "running sequence: Turn on DJ"
						
                        def currentSwitch = currentSwitchState()
                        def currentProj = currentProjLiftState()
                        def currentSpeaker = currentSpeakerState()
                        def currentPower = currentSwitchPower()
                        
                        if (currentSwitch == "off"){                   
							runIn(1, EntOn)
                        	runIn(14, DenOn)
                        	runIn(18, DenDJ)
                        	}
                        if (currentPower > 103){                   
                        	runIn(4, BluOn)
                        	}
                        if (currentSwitch == "on"){                   
                        	runIn(1, DenDJ)
                        	}
						if (currentProj == "on"){                   
                            runIn(3, ProOff)
                        	runIn(4, ProjUp)
                        	}
                        if (currentSpeaker == "off"){                   
                        	runIn(2, speakersOn)
                        	}
                        }
                        
                       /* runIn(1, EntOn)
                        runIn(2, speakersOn)
                        runIn(14, DenOn)
                        runIn(18, DenDJ)
                        } */ //Old automatio
                       
                        
                        
                    }


def currentSwitchState(){
	def currentState = theSwitch.currentValue("switch")
    
    return currentState
    
    }
def currentSwitchPower(){
	def currentState = powerMeter.currentValue("power")
    
    return currentState
    
    }

def currentProjLiftState(){
	def currentState = projSwitch.currentValue("switch")
    
    return currentState
    
    }
    
def currentSpeakerState(){
	def currentState = speaker1.currentValue("switch")
    
    return currentState
}

def speakersOn() {
	log.debug "Speakers on"
    speaker1.on()
    speaker2.on()
    speaker3.on()
    speaker4.on()
    speaker5.on()
    speaker6.on()
    }
    
def speakersOff() {
	log.debug "Speakers on"
    speaker1.off()
    speaker2.off()
    speaker3.off()
    speaker4.off()
    speaker5.off()
    speaker6.off()
    }
    
def ProjDown() {
	log.debug "Projector coming out"
    projSwitch.on()
    }
    
def ProjUp() {
	log.debug "Projector coming out"
    projSwitch.off()
    }
    
def BlindDown() {
	log.debug "Blinds going down"
    blind1.on()
    blind2.on()
    blind3.on()
    }
    
def BlindUp() {
	log.debug "Blinds going up"
    blind1.off()
    blind2.off()
    blind3.off()
    }      

def EntOn() {
	log.debug "Entertainment switch on"
    theSwitch.on()
//    Entertainment.On
    }
def EntOff() {
	log.debug "Entertainment switch off"
    theSwitch.off()
//    Entertainment.On
    }
def ProOn() {
	log.debug "Projector turned on"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:2,1,38000,1,1,343,170,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,20,22,20,22,62,22,62,22,62,22,62,22,62,22,20,22,62,22,62,22,20,22,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
        sendHubCommand(new physicalgraph.device.HubAction("sendir,1:2,1,38000,1,1,343,170,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,20,22,20,22,62,22,62,22,62,22,62,22,62,22,20,22,62,22,62,22,20,22,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
        }
def ProOff() {
	log.debug "Projector turned off"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:2,2,37764,1,1,24,484,15,69,19,64,22,21,16,27,14,29,14,29,14,29,12,72,20,63,22,21,19,64,22,21,20,64,22,21,21,63,22,21,22,62,22,21,22,21,22,21,22,62,22,21,22,21,22,62,22,21,22,62,22,62,22,62,22,21,22,62,22,62,22,21,22,1540,343,168,22,62,22,62,22,21,22,21,22,21,22,21,22,21,22,62,22,62,22,21,22,62,22,21,22,62,22,21,22,62,22,21,22,62,22,21,22,21,22,21,22,62,22,21,22,21,22,62,22,21,22,62,22,62,22,62,22,21,22,62,22,62,22,21,22,1541,343,168,22,62,22,62,22,21,22,21,22,21,22,21,22,21,22,62,22,62,22,21,22,62,22,21,22,62,22,21,22,62,22,21,22,62,22,21,21,22,18,25,15,69,18,25,12,31,10,74,12,31,8,76,9,75,9,75,7,36,4,80,6,78,5,3700\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
        }
def DenOn() {
	log.debug "Denon Av Turned on/off"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,2840,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,2840,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
   }
def DenBlu() {
	log.debug "Denon Switched to Bluray"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,6,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2841,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2842,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def DenNex() {
	log.debug "Denon Switched to Nexus"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,48,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,48,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,48,16,16,16,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def DenApp() {
	log.debug "Denon Switched to AppleTV"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,38226,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,16,16,48,16,16,16,2850,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,16,16,48,16,16,16,2850,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,16,16,48,16,16,16,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def DenDLNA() {
	log.debug "Denon Switched to DLNA"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,16,16,48,16,16,16,3800\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def DenDJ() {
	log.debug "Denon Switched to DJ"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,2828,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,4878\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def BluOn() {
	log.debug "BluRay Player Switched On/Off"
    def deviceNetworkId = "C0A8019B:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,37000,1,1,128,64,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,48,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,16,16,16,16,48,16,2734\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
def BluTV() {
	log.debug "BluRay Player TV Guide"
    def deviceNetworkId = "C0A80F81:1386"
		sendHubCommand(new physicalgraph.device.HubAction("sendir,1:3,1,36337,1,1,127,63,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,2726,127,63,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,3600\r", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}"))
    }
                    
         
/*		def theCom
			if (value == "pushed" && buttonNumber == 1) {
			log.debug "Sending power on/off Denon"
			theCom = "sendir,1:3,1,38000,1,1,11,29,10,70,10,30,10,30,10,30,10,70,10,30,10,30,10,30,10,30,10,30,10,70,10,70,10,30,10,30,10,1776,10,30,10,70,10,30,10,30,10,30,10,30,10,70,10,70,10,70,10,70,10,70,10,30,10,30,10,70,10,70,10,1776,10,30,10,70,10,30,10,30,10,30,10,30,10,70,10,70,10,70,10,70,10,70,10,30,10,30,10,70,10,70,10,1776,10,30,10,70,10,30,10,30,10,30,10,30,10,70,10,70,10,70,10,70,10,70,10,30,10,30,10,70,10,70,10,1776,10,70,10,30,10,30,10,30,10,70,10,30,10,30,10,30,10,30,10,30,10,70,10,70,10,30,10,30,10,1617\r"
            }
            else if (value == "pushed" && buttonNumber == 2) {
			log.debug "Sending power on/off panasonic"
			theCom = "sendir,1:3,1,37000,1,1,128,64,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,48,16,48,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,16,16,16,16,48,16,2734\r"
            }
            else if (value == "pushed" && buttonNumber == 3) {
			log.debug "Sending bluray button denon"
			theCom = "sendir,1:3,6,38109,1,1,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2841,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,2842,128,64,16,16,16,16,16,48,16,16,16,48,16,16,16,48,16,16,16,16,16,48,16,16,16,16,16,48,16,48,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,16,48,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,16,16,16,16,48,16,16,16,48,16,48,16,16,16,48,16,16,16,16,16,16,16,16,16,48,16,48,16,48,16,48,16,48,16,16,16,3800\r"
			}
*/              	
        




/*	def locks = find('locks', buttonNumber, value)
	if (locks != null) toggle(locks)

	def sonos = find('sonos', buttonNumber, value)
	if (sonos != null) toggle(sonos)

	def mode = find('mode', buttonNumber, value)
	if (mode != null) changeMode(mode)

	def phrase = find('phrase', buttonNumber, value)
	if (phrase != null) location.helloHome.execute(phrase)

	def textMessage = findMsg('textMessage', buttonNumber)

	def notifications = find('notifications', buttonNumber, value)
	if (notifications?.toBoolean()) sendPush(textMessage ?: "Button $buttonNumber was pressed" )

	def phone = find('phone', buttonNumber, value)
	if (phone != null) sendSms(phone, textMessage ?:"Button $buttonNumber was pressed")

    def sirens = find('sirens', buttonNumber, value)
    if (sirens != null) toggle(sirens)
*/


def find(type, buttonNumber, value) {
	def preferenceName = type + "_" + buttonNumber + "_" + value
	def pref = settings[preferenceName]
	if(pref != null) {
		log.debug "Found: $pref for $preferenceName"
	}

	return pref
}

def findMsg(type, buttonNumber) {
	def preferenceName = type + "_" + buttonNumber
	def pref = settings[preferenceName]
	if(pref != null) {
		log.debug "Found: $pref for $preferenceName"
	}

	return pref
}

def toggle(devices) {
	log.debug "toggle: $devices = ${devices*.currentValue('switch')}"

	if (devices*.currentValue('switch').contains('on')) {
		devices.off()
	}
	else if (devices*.currentValue('switch').contains('off')) {
		devices.on()
	}
	else if (devices*.currentValue('lock').contains('locked')) {
		devices.unlock()
	}
	else if (devices*.currentValue('alarm').contains('off')) {
        devices.siren()
    }
	else {
		devices.on()
	}
}

def changeMode(mode) {
	log.debug "changeMode: $mode, location.mode = $location.mode, location.modes = $location.modes"

	if (location.mode != mode && location.modes?.find { it.name == mode }) {
		setLocationMode(mode)
	}
}

// execution filter methods
private getAllOk() {
	modeOk && daysOk && timeOk
}

private getModeOk() {
	def result = !modes || modes.contains(location.mode)
	log.trace "modeOk = $result"
	result
}

private getDaysOk() {
	def result = true
	if (days) {
		def df = new java.text.SimpleDateFormat("EEEE")
		if (location.timeZone) {
			df.setTimeZone(location.timeZone)
		}
		else {
			df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
		}
		def day = df.format(new Date())
		result = days.contains(day)
	}
	log.trace "daysOk = $result"
	result
}

private getTimeOk() {
	def result = true
	if (starting && ending) {
		def currTime = now()
		def start = timeToday(starting).time
		def stop = timeToday(ending).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	log.trace "timeOk = $result"
	result
}

private hhmm(time, fmt = "h:mm a")
{
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}

private hideOptionsSection() {
	(starting || ending || days || modes) ? false : true
}

private timeIntervalLabel() {
	(starting && ending) ? hhmm(starting) + "-" + hhmm(ending, "h:mm a z") : ""
}
