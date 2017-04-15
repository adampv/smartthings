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
    name: "iTach Assigner (Bedroom TV)",
    namespace: "AdamV",
    author: "AdamV",
    description: "Assign buttons to iTach Commands",
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

def executeHandlers(buttonNumber, value) {
	log.debug "executeHandlers: $buttonNumber - $value"
    	//def deviceNetworkId = "C0A80F81:1386"  
        def deviceNetworkId = "C0A8019E:1386"
        // def deviceNetworkId = "C0A80149:1386"
		def theCom
			if (value == "pushed" && buttonNumber == 1) {
			log.debug "Sending power on/off"
			theCom = "sendir,1:3,1,39936,1,1,9,1046,96,24,48,24,24,24,C,D,C,D,D,C,D,D,D,24,1031,B,C,D,C,D,C,D,D,C,D,D,D,24,5111\r"
            }
            else if (value == "pushed" && buttonNumber == 2) {
			log.debug "Sending OK"
			theCom = "sendir,1:2,1,38000,1,1,343,169,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,20,22,20,22,62,22,20,22,62,22,20,22,62,22,62,22,62,22,62,22,20,22,3800\r"
			}
            else if (value == "pushed" && buttonNumber == 3) {
			log.debug "Sending Up"
			theCom = "sendir,1:2,1,38000,1,1,343,170,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,62,22,62,22,62,22,62,22,20,22,20,22,62,22,20,22,3800\r"
			}
            else if (value == "pushed" && buttonNumber == 4) {
			log.debug "Sending Down"
			theCom = "sendir,1:2,1,38000,1,1,343,168,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,62,22,20,22,20,22,62,22,62,22,20,22,62,22,62,22,20,22,62,22,62,22,20,22,20,22,62,22,20,22,3800\r"
			}
            else if (value == "pushed" && buttonNumber == 5) {
			log.debug "Sending bathroom zone toggle"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,C,C,B,B,C,B,C,C,B,B,C,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,C,C,C,C,C,C,C,B,B,B,B,B,B,B,B,22,1069\r"
			}
            else if (value == "pushed" && buttonNumber == 6) {
			log.debug "Sending Bedroom Zone Toggle"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,C,C,B,B,C,B,C,C,B,B,C,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,C,C,C,C,C,C,C,C,B,B,B,B,B,B,B,22,1069\r"
			}
            else if (value == "pushed" && buttonNumber == 7) {
			log.debug "Sending mute"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,C,B,C,C,C,B,C,B,B,C,B,B,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,C,B,C,C,C,B,C,B,B,C,B,B,B,22,5128\r"
			}
            else if (value == "pushed" && buttonNumber == 8) {
			log.debug "Sending bass-"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,B,C,B,C,B,C,C,C,C,B,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,C,C,C,C,C,C,C,C,B,B,B,B,B,B,B,22,1069\r"
			}
           	else if (value == "pushed" && buttonNumber == 9) {
			log.debug "Sending bass+"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,B,C,B,C,B,C,C,C,C,B,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,C,C,C,C,C,C,C,B,B,B,B,B,B,B,B,22,1069\r"
			}
            else if (value == "pushed" && buttonNumber == 11) {
			log.debug "Sending volDown"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,C,B,C,C,C,C,C,C,B,C,B,B,B,B,22,1068,339,169,B,C,B,C,C,B,C,B,C,B,C,B,21,63,21,21,B,C,B,21,63,21,21,B,21,21,C,C,C,C,C,B,21,21,B,21,63,21,63,20,63,20,1069,4,7,332,169,20,63,20,21,B,21,21,C,B,21,21,B,21,21,B,21,21,21,63,21,63,21,21,21,63,21,21,B,20,63,20,21,B,21,21,C,C,C,C,C,B,C,B,21,63,21,63,21,63,20,5128\r"
			}
            else if (value == "pushed" && buttonNumber == 12) {
			log.debug "Sending menu"
			theCom = "sendir,1:2,1,38000,1,1,343,169,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,20,22,62,22,20,22,62,22,62,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,20,22,62,22,62,22,20,22,3800\r"
			}
            else if (value == "pushed" && buttonNumber == 13) {
			log.debug "Sending volUp"
			theCom = "sendir,1:1,1,40064,1,1,338,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,B,C,C,C,C,B,C,B,C,B,B,B,B,22,1068,340,169,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,B,C,C,C,C,B,C,B,C,B,B,B,B,22,1069,340,169,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,B,C,C,C,C,B,C,B,C,B,B,B,B,22,5128\r"
			}
            else if (value == "pushed" && buttonNumber == 14) {
			log.debug "Sending Trebble-"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,B,C,B,C,B,C,C,C,C,B,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,C,C,C,C,C,C,C,C,B,B,B,B,B,B,22,1069\r"
			}
            else if (value == "pushed" && buttonNumber == 15) {
			log.debug "Sending colourmode"
			theCom = "sendir,1:2,1,38000,1,1,343,170,22,62,22,62,22,20,22,20,22,20,22,20,22,20,22,62,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,20,22,62,22,62,22,62,22,62,22,20,22,20,22,20,22,62,22,20,22,20,22,20,22,20,22,62,22,62,22,62,22,20,22,3800\r"
			}
            else if (value == "pushed" && buttonNumber == 16) {
			log.debug "Sending treble+"
			theCom = "sendir,1:1,1,40064,1,1,340,169,22,63,22,21,B,C,C,B,C,B,C,B,C,B,B,C,B,C,B,B,B,C,B,C,B,C,C,C,C,B,C,B,C,B,22,1068,A,B,C,B,C,C,B,C,B,C,B,C,B,B,C,B,C,C,B,C,C,C,C,C,C,B,C,B,B,B,B,B,B,22,1069\r"
			}
    	
        sendHubCommand(new physicalgraph.device.HubAction("""$theCom\r\n""", physicalgraph.device.Protocol.LAN, "${deviceNetworkId}")) 
}

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
