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
 *  Assign Buttons to Wireless Wall controllers
 *
 *  TODO
 *  Get better dimming frequency than 1Hz
 *  Turn into Parent / Child app to avoid clutter for multiple switches
 *
 *  Version 1.6
 *  Author: AdamV
 *  Date: 2016-01-25
 *
 *  Changes since 1.5:
 *  - Added more options for routines based off various click events
 *	
 *	Changes since 1.4:
 *	- Click Hold action now works as well! Thanks to Miles Frankland
 *	- Cleaned up log reporting
 * 
 *	Changes since 1.3:
 *	- Double Clicks working now! Thanks to Stuart Buchanan
 *	- Cleaned up labels in setup
 *  To set colour and level of lights on push/hold events, connect to a routine, use smart lighting or Rule Machine
 */
 
definition(
    name: "Button controller with dimming, double clicks, & click-holds",
    namespace: "AdamV",
    author: "AdamV",
    description: "Assign events to button pushes, hold start, whilst held, & hold end to swicthes and level switches.For Z-Wave.me Secure Wireless Wall controller (ZME_WALLC-S), Z-Wave.me Wall controller 2 (ZME_WALLC-2), Popp Wall C Forever, Devolo Wall Switch & Z-Wave.me Key Fob",
    category: "Convenience",
    iconUrl: "http://94.23.40.33/smartthings/assets/rocker.png",
	iconX2Url: "http://94.23.40.33/smartthings/assets/rocker.png",
	iconX3Url: "http://94.23.40.33/smartthings/assets/rocker.png",
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
        section([mobileOnly:true]) {
            label title: "Name this Switch configuration:", required: true
            }
        section {
            input "buttonDevice", "capability.button", title: "Controller", multiple: false, required: true
			state.smoothness = 1
            state.dimDelay = 1000
			}
     /*   section {
            paragraph "\n\n Set Dimming Smoothness \n 1 = once a second, 2 = twice a second etc"
            input "smoothness", "number", title: "Smoothness of dimming (1-4)", required: true, range: "1..4", submitOnChange: true
                if(smoothness == 1) {
                    state.smoothness = 1
                    state.dimDelay = 1000
                }
                else if(smoothness == 2) {
                    state.smoothness = 2
                    state.dimDelay = 500
                }
                else if(smoothness == 3) {
                    state.smoothness = 3
                    state.dimDelay = 334
                }
                else if(smoothness == 4) {
                    state.smoothness = 4
                    state.dimDelay = 250
                }
                else if(!smoothness) {
                    state.smoothness = 1
                    state.dimDelay = 1000
                }
            }
        */
        section {
            input "dimIncrement", "number", title: "Dimming Increment (5-50)", required: false, range: "5..50", submitOnChange: true
                if(dimIncrement > 0) {
                    state.dimIncrement = dimIncrement
                }
                else if(!dimIncrement) {
                    state.dimIncrement = 10
                }
            }
            section(title: "More options", hidden: hideOptionsSection(), hideable: true) {

            def timeLabel = timeIntervalLabel()

            href "timeIntervalInput", title: "Only during a certain time", description: timeLabel ?: "Tap to set", state: timeLabel ? "complete" : null

            input "days", "enum", title: "Only on certain days of the week", multiple: true, required: false,
                options: ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

            input "modes", "mode", title: "Only when mode is", multiple: true, required: false
        }

    }
}

def configureButton1() {
    dynamicPage(name: "configureButton1", title: "\n\n Setup the FIRST button here:\n\n",
        nextPage: "configureButton2", uninstall: configured()) {
        def phrases = location.helloHome?.getPhrases()*.label
            if (phrases) {
                section("Button Press") {
                    log.trace phrases
                    input "Device1press", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
                    input "Device1pressDimUp", "capability.switch", title: "Device(s) to increment up", multiple: true, required: false
                    input "Device1pressRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
                }                 
        section ("On button hold (only fired once)")  {
            input "Device1longholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device1longholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device1longholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1longholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("Whilst button is held (fired every 1s whilst held)")  {
            input "Device1heldDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1heldDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is released")  {
            input "Device1ReleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device1ReleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1ReleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
       	section ("When button is double clicked")  {
            input "Device1DoubleSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device1DoubleRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device1DoubleDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1DoubleDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
        section ("When button is click-held (only fired once)")  {
            input "Device1clickholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device1clickholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device1clickholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1clickholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is click-held-released (only fired once)")  {
            input "Device1clickholdreleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device1clickholdreleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device1clickholdreleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        }
    }
}
def configureButton2() {
    dynamicPage(name: "configureButton2", title: "\n\n Setup the SECOND button here:\n\n",
        nextPage: "configureButton3", uninstall: configured()) {
        def phrases = location.helloHome?.getPhrases()*.label
            if (phrases) {
                section("Button Press") {
                    log.trace phrases
                    input "Device2press", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
                    input "Device2pressDimUp", "capability.switch", title: "Device(s) to increment up", multiple: true, required: false
                    input "Device2pressRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
                }  
        section ("On button hold (only fired once)")  {
            input "Device2longholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device2longholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device2longholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2longholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("Whilst button is held (fired every 1s whilst held)")  {
            input "Device2heldDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2heldDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is released")  {
            input "Device2ReleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device2ReleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2ReleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
      	section ("When button is double clicked")  {
            input "Device2DoubleSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device2DoubleRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device2DoubleDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2DoubleDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
        section ("When button is click-held (only fired once)")  {
            input "Device2clickholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device2clickholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device2clickholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2clickholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is click-held-released (only fired once)")  {
            input "Device2clickholdreleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device2clickholdreleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device2clickholdreleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        }
    }
}

def configureButton3() {
    dynamicPage(name: "configureButton3", title: "\n\n Setup the THIRD button here:\n\n",
        nextPage: "configureButton4", uninstall: configured()) {
        def phrases = location.helloHome?.getPhrases()*.label
            if (phrases) {
                section("Button Press") {
                    log.trace phrases
                    input "Device3press", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
                    input "Device3pressDimDown", "capability.switch", title: "Device(s) to increment down", multiple: true, required: false
                    input "Device3pressRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
                }  
        section ("On button hold (only fired once)")  {
            input "Device3longholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device3longholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device3longholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3longholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("Whilst button is held (fired every 1s whilst held)")  {
            input "Device3heldDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3heldDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is released")  {
            input "Device3ReleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device3ReleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3ReleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
        section ("When button is double clicked")  {
            input "Device3DoubleSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device3DoubleRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device3DoubleDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3DoubleDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
       	section ("When button is click-held (only fired once)")  {
            input "Device3clickholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device3clickholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device3clickholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3clickholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is click-held-released (only fired once)")  {
            input "Device3clickholdreleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device3clickholdreleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device3clickholdreleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        }
    }
}
def configureButton4() {
    dynamicPage(name: "configureButton4", title: "\n\n Setup the FOURTH button here:\n\n",
        install: true, uninstall: true ) {
        def phrases = location.helloHome?.getPhrases()*.label
            if (phrases) {
                section("Button Press") {
                    log.trace phrases
                    input "Device4press", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
                    input "Device4pressDimDown", "capability.switch", title: "Device(s) to increment down", multiple: true, required: false
                    input "Device4pressRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
                } 
        section ("On button hold (only fired once)")  {
            input "Device4longholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device4longholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device4longholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4longholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("Whilst button is held (fired every 1s whilst held)")  {
            input "Device4heldDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4heldDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is released")  {
            input "Device4ReleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device4ReleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4ReleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
      	section ("When button is double clicked")  {
            input "Device4DoubleSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device4DoubleRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device4DoubleDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4DoubleDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
        	}
        section ("When button is click-held (only fired once)")  {
            input "Device4clickholdSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device4clickholdRoutine", "enum", title: "Routine(s) to trigger", required: false, options: phrases
            input "Device4clickholdDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4clickholdDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        section ("When button is click-held-released (only fired once)")  {
            input "Device4clickholdreleaseSwitch", "capability.switch", title: "Device(s) to switch on/off", multiple: true, required: false
            input "Device4clickholdreleaseDimUp", "capability.switchLevel", title: "Device(s) to Dim / Roll Up", multiple: true, required: false
            input "Device4clickholdreleaseDimDown", "capability.switchLevel", title: "Device(s) to Dim / Roll Down", multiple: true, required: false
            }
        }
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
    return settings["Device$idxholdStartDimUp"] ||
        settings["Device$idxholdStartDimUp"]

}

def buttonEvent(evt){
    if(allOk) {
        //log.debug(evt.data)
        
        String [] extra = evt.data.split( "," )
        String extrapayload = extra[ 0 ]  

        String [] sections = extrapayload.split( ":" )
    //    log.debug("sections: $sections")

        String payload = sections[ 1 ]
//      log.debug( "Command: $payload" )
        
        String payload2 = payload.replaceAll("[/}/g]","")
        Integer payload3 = payload2.toInteger()
        
        def buttonNumber = payload3 // why doesn't jsonData work? always returning [:]
        def value = evt.value
    //  log.debug "buttonEvent: $evt.name = $evt.value ($evt.data)"
      log.debug "button: $buttonNumber, value: $value"

        atomicState.startHoldTime = 0
        atomicState.buttonIsHolding = false
    //  atomicState.pulseDelay = 600
        atomicState.currentButton = -1
        if (Device1heldDimUp != null) {
        	if(Device1heldDimUp[0].currentSwitch == "off") atomicState.device1heldDimUpLevel = 0
    		else atomicState.device1heldDimUpLevel = Device1heldDimUp[0].currentLevel
            // log.debug(" level: $atomicState.device1heldDimUpLevel")
            }
        if (Device2heldDimUp != null) {
            if(Device2heldDimUp[0].currentSwitch == "off") atomicState.device2heldDimUpLevel = 0
    		else atomicState.device2heldDimUpLevel = Device2heldDimUp[0].currentLevel
        	// atomicState.device2heldDimUpLevel = Device2heldDimUp.currentLevel
        	// log.debug("level: $atomicState.device2heldDimUpLevel")
            }
       	if (Device3heldDimUp != null) {  
        	if(Device3heldDimUp[0].currentSwitch == "off") atomicState.device3heldDimUpLevel = 0
    		else atomicState.device3heldDimUpLevel = Device3heldDimUp[0].currentLevel
            // atomicState.device3heldDimUpLevel = Device3heldDimUp.currentLevel
        	// log.debug("$atomicState.device3heldDimUpLevel")
            }
        if (Device4heldDimUp != null) {
        	if(Device4heldDimUp[0].currentSwitch == "off") atomicState.device4heldDimUpLevel = 0
    		else atomicState.device4heldDimUpLevel = Device4heldDimUp[0].currentLevel
            // atomicState.device4heldDimUpLevel = Device4heldDimUp.currentLevel
        	// log.debug("$atomicState.device4heldDimUpLevel")
            }
        if (Device1heldDimDown != null) {
        	if(Device1heldDimDown[0].currentSwitch == "off") atomicState.device1heldDimDownLevel = 0
    		else atomicState.device1heldDimDownLevel = Device1heldDimDown[0].currentLevel
            // atomicState.device1heldDimDownLevel = Device1heldDimDown.currentLevel
        	// log.debug("$atomicState.device1heldDimDownLevel")
            }
        if (Device2heldDimDown != null) {
        	if(Device2heldDimDown[0].currentSwitch == "off") atomicState.device2heldDimDownLevel = 0
    		else atomicState.device2heldDimDownLevel = Device2heldDimDown[0].currentLevel
            // atomicState.device2heldDimDownLevel = Device2heldDimDown.currentLevel
        	// log.debug("$atomicState.device2heldDimDownLevel")
            }
        if (Device3heldDimDown != null) {
        	if(Device3heldDimDown[0].currentSwitch == "off") atomicState.device3heldDimDownLevel = 0
    		else atomicState.device3heldDimDownLevel = Device3heldDimDown[0].currentLevel
            // atomicState.device3heldDimDownLevel = Device3heldDimDown.currentLevel
        	// log.debug("$atomicState.device3heldDimDownLevel")
            }
        if (Device4heldDimDown != null) {
        	if(Device4heldDimDown[0].currentSwitch == "off") atomicState.device4heldDimDownLevel = 0
    		else atomicState.device4heldDimDownLevel = Device4heldDimDown[0].currentLevel
            // atomicState.device4heldDimDownLevel = Device4heldDimDown.currentLevel
        	// log.debug("$atomicState.device4heldDimDownLevel")
            }
        executeHandlers(buttonNumber, value)
		// log.debug("execute handlers")
    }
}

def startPulsing() {
    // log.debug ("pulse")
    def currentTime = now(); //milliseconds, please. 
    if( atomicState.currentButton == -1 ) { 
    	return 
        } 
    if( atomicState.buttonIsHolding == false ) {
        return
    	}
    def maxPulse = 18 * state.smoothness
    // log.debug("maxPulse: $maxPulse")
    if( atomicState.pulseNumber > maxPulse ) { return } 
    def button = atomicState.currentButton
    
    // if( currentTime - atomicState.startHoldTime >= pulseDelay ) {
   // if( currentTime - atomicState.startHoldTime >= state.dimDelay ) {
        atomicState.pulseNumber = atomicState.pulseNumber + 1
        log.debug("pulse number: $atomicState.pulseNumber")
        
        sendButtonHoldContinue();
        // log.debug ("sent pulse")
        //}
        
        // Delay?!
         runIn( 1, startPulsing )
        // startPulsing([delay: state.dimDelay])
}

def sendButtonHoldContinue() {
    if( atomicState.currentButton == -1 ) { return; }
    if( atomicState.buttonIsHolding == false ) { return; }
    if( atomicState.direction == null ) { return; }
    
    if( atomicState.direction == "Up" && atomicState.currentButton == 1 ) {
        log.debug ("going up with 1")
        // def newLevel = Device1heldDimUp.currentLevel + state.dimIncrement
        if( atomicState.device1heldDimUpLevel >= 100 ) { return; }
        def newLevel = atomicState.device1heldDimUpLevel + state.dimIncrement
        atomicState.device1heldDimUpLevel = atomicState.device1heldDimUpLevel + state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device1heldDimUp.setLevel(newLevel)
        // Device1heldDimUp.levelUp()
        }
    else if( atomicState.direction == "Down" && atomicState.currentButton == 1 ) {
        log.debug ("going down with 1")
        // def newLevel = Device1heldDimDown.currentLevel - state.dimIncrement
       if( atomicState.device1heldDimDownLevel <= 0 ) { return; }
       def newLevel = atomicState.device1heldDimDownLevel - state.dimIncrement
        atomicState.device1heldDimDownLevel = atomicState.device1heldDimDownLevel - state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device1heldDimDown.setLevel(newLevel)
        // Device1heldDimDown.levelDown()
        } 
    else if( atomicState.direction == "Up" && atomicState.currentButton == 2 ) {
        log.debug ("going up with 2")
        // def newLevel = Device2heldDimUp.currentLevel + state.dimIncrement
        if( atomicState.device2heldDimUpLevel >= 100 ) { return; }
        def newLevel = atomicState.device2heldDimUpLevel + state.dimIncrement
        atomicState.device2heldDimUpLevel = atomicState.device2heldDimUpLevel + state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device2heldDimUp.setLevel(newLevel)
        // Device2heldDimUp.levelUp()
        }
    else if( atomicState.direction == "Down" && atomicState.currentButton == 2 ) {
        log.debug ("going down with 2")
        // def newLevel = Device2heldDimDown.currentLevel - state.dimIncrement
        if( atomicState.device2heldDimDownLevel <= 0 ) { return; }
        def newLevel = atomicState.device2heldDimDownLevel - state.dimIncrement
        atomicState.device2heldDimDownLevel = atomicState.device2heldDimDownLevel - state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device2heldDimDown.setLevel(newLevel)
        // Device2heldDimDown.levelDown()
        } 
    else if( atomicState.direction == "Up" && atomicState.currentButton == 3 ) {
        log.debug ("going up with 3")
        // def newLevel = Device3heldDimUp.currentLevel + state.dimIncrement
        if( atomicState.device3heldDimUpLevel >= 100 ) { return; }
        def newLevel = atomicState.device3heldDimUpLevel + state.dimIncrement
        atomicState.device3heldDimUpLevel = atomicState.device3heldDimUpLevel + state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device3heldDimUp.setLevel(newLevel)
        // Device3heldDimUp.levelUp()
        }
    else if( atomicState.direction == "Down" && atomicState.currentButton == 3 ) {
        log.debug ("going down with 3")
        // def newLevel = Device3heldDimDown.currentLevel - state.dimIncrement
        if( atomicState.device3heldDimDownLevel <= 0 ) { return; }
        def newLevel = atomicState.device3heldDimDownLevel - state.dimIncrement
        atomicState.device3heldDimDownLevel = atomicState.device3heldDimDownLevel - state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device3heldDimDown.setLevel(newLevel)
        // Device3heldDimDown.levelDown()
        } 
    else if( atomicState.direction == "Up" && atomicState.currentButton == 4 ) {
        log.debug ("going up with 4")
        // def newLevel = Device4heldDimUp.currentLevel + state.dimIncrement
        if( atomicState.device4heldDimUpLevel >= 100 ) { return; }
        def newLevel = atomicState.device4heldDimUpLevel + state.dimIncrement
        atomicState.device4heldDimUpLevel = atomicState.device4heldDimUpLevel + state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device4heldDimUp.setLevel(newLevel)
        // Device4heldDimUp.levelUp()
        }
    else if( atomicState.direction == "Down" && atomicState.currentButton == 4 ) {
        log.debug ("going down with 4")
        // def newLevel = Device4heldDimDown.currentLevel - state.dimIncrement
        if( atomicState.device4heldDimDownLevel <= 0 ) { return; }
        def newLevel = atomicState.device4heldDimDownLevel - state.dimIncrement
        atomicState.device4heldDimDownLevel = atomicState.device4heldDimDownLevel - state.dimIncrement
        log.debug("newLevel is $newLevel")
        Device4heldDimDown.setLevel(newLevel)
        // Device4heldDimDown.levelDown()
        } 
    
}

def onButtonHoldStart() {
    atomicState.startHoldTime = now()
    atomicState.buttonIsHolding = true
    atomicState.pulseNumber = 0
    // log.debug("onbuttonholdstart")
    startPulsing()
}

def onButtonHoldEnd() {
    atomicState.buttonIsHolding = false;
    // log.debug ("HoldRelease event sent")
    atomicState.currentButton = -1;
    atomicState.direction = null
    unschedule("startPulsing")
}
def executeHandlers(buttonNumber, value) {
    log.debug "executeHandlers: $buttonNumber - $value"
            if (value == "pushed" && buttonNumber == 1) {           
                if (Device1press != null) toggle(Device1press)
                if (Device1pressRoutine != null) location.helloHome?.execute(settings.Device1pressRoutine)
                if (Device1pressDimUp != null) {
                    def newLevel = Device1pressDimUp[0].currentLevel + state.dimIncrement
                    Device1pressDimUp.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "held" && buttonNumber == 1) {
                atomicState.currentButton = buttonNumber
                if (Device1longholdSwitch != null) toggle(Device1longholdSwitch)
                if (Device1longholdRoutine != null) location.helloHome?.execute(settings.Device1longholdRoutine)
                if (Device1longholdDimUp != null) {
                    def newLevel = Device1longholdDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device1longholdDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device1longholdDimDown != null) {
                    def newLevel = Device1longholdDimDown[0].currentLevel - state.dimIncrement
                    Device1longholdDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber long hold going down"
                }
                if (Device1heldDimUp != null) {
                    atomicState.direction = "Up"
                log.debug "Button $buttonNumber Hold Start to go up"
                }
                if (Device1heldDimDown != null) {
                    atomicState.direction = "Down"
                log.debug "Button $buttonNumber Hold Start to go down"
                }
                onButtonHoldStart()
                log.debug "$buttonNumber $value"
            }
            else if (value == "holdRelease" && buttonNumber == 1) {         
                onButtonHoldEnd()
                if (Device1ReleaseSwitch != null) toggle(Device1ReleaseSwitch)
                if (Device1ReleaseDimUp != null) {
                //     Device1ReleaseDimUp.levelUp()
                    def newLevel = Device1ReleaseDimUp[0].currentLevel + state.dimIncrement
                    Device1ReleaseDimUp.setLevel(newLevel)
                }
                if (Device1ReleaseDimDown != null) {
                //     Device1ReleaseDimDown.levelDown()
                    def newLevel = Device1ReleaseDimDown[0].currentLevel - state.dimIncrement
                    Device1ReleaseDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "doubleClick" && buttonNumber == 1) {
                if (Device1DoubleSwitch != null) toggle(Device1DoubleSwitch)
                if (Device1DoubleRoutine != null) location.helloHome?.execute(settings.Device1DoubleRoutine)
                if (Device1DoubleDimUp != null) {
                    def newLevel = Device1DoubleDimUp[0].currentLevel + state.dimIncrement
                    Device1DoubleDimUp.setLevel(newLevel)
                }
                if (Device1DoubleDimDown != null) {
                    def newLevel = Device1DoubleDimDown[0].currentLevel - state.dimIncrement
                    Device1DoubleDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "clickHoldStart" && buttonNumber == 1) {
                atomicState.currentButton = buttonNumber
                if (Device1clickholdSwitch != null) toggle(Device1clickholdSwitch)
                if (Device1clickholdRoutine != null) location.helloHome?.execute(settings.Device1clickholdRoutine)
                if (Device1clickholDimUp != null) {
                    def newLevel = Device1clickholdDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device1clickholdDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber click hold start going up"
                }
                if (Device1clickholdDimDown != null) {
                    def newLevel = Device1clickholdDimDown[0].currentLevel - state.dimIncrement
                    Device1clickholDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber click hold start going down"
                }
            }
           	else if (value == "clickHoldStop" && buttonNumber == 1) {
                atomicState.currentButton = buttonNumber
                if (Device1clickholdreleaseSwitch != null) toggle(Device1longholdSwitch)
                if (Device1clickholdreleaseDimUp != null) {
                    def newLevel = Device1clickholdreleaseDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device1clickholdreleaseDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device1clickholdreleaseDimDown != null) {
                    def newLevel = Device1clickholdreleaseDimDown[0].currentLevel - state.dimIncrement
                    Device1clickholdreleaseDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber long hold going down"
                }
            }
            else if (value == "pushed" && buttonNumber == 2) {
                if (Device2press != null) toggle(Device2press)
                if (Device2pressRoutine != null) location.helloHome?.execute(settings.Device2pressRoutine)
                if (Device2pressDimUp != null) {
                    def newLevel = Device2pressDimUp[0].currentLevel + state.dimIncrement
                    Device2pressDimUp.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "held" && buttonNumber == 2) {
                atomicState.currentButton = buttonNumber
                if (Device2longholdSwitch != null) toggle(Device2longholdSwitch)
                if (Device2longholdRoutine != null) location.helloHome?.execute(settings.Device2longholdRoutine)
                if (Device2longholdDimUp != null) {
                    // Device2longholdDimUp.levelUp()
                    def newLevel = Device2longholdDimUp[0].currentLevel + state.dimIncrement
                    Device2longholdDimUp.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device2longholdDimDown != null) {
                    // Device2longholdDimDown.levelDown()
                    def newLevel = Device2longholdDimDown[0].currentLevel - state.dimIncrement
                    Device2longholdDimDown.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going down"
                }
                if (Device2heldDimUp != null) {
                    atomicState.direction = "Up"
                log.debug "Button $buttonNumber Hold Start to go up"
                }
                if (Device2heldDimDown != null) {
                    atomicState.direction = "Down"
                log.debug "Button $buttonNumber Hold Start to go down"
                }
                onButtonHoldStart()
                log.debug "$buttonNumber $value"
            }
            else if (value == "holdRelease" && buttonNumber == 2) {         
                onButtonHoldEnd()
                if (Device2ReleaseSwitch != null) toggle(Device2ReleaseSwitch)
                if (Device2ReleaseDimUp != null) {
                    // Device2ReleaseDimUp.levelUp()
                    def newLevel = Device2ReleaseDimUp[0].currentLevel + state.dimIncrement
                    Device2ReleaseDimUp.setLevel(newLevel)
                }
                if (Device2ReleaseDimDown != null) {
                    // Device2ReleaseDimDown.levelDown()
                    def newLevel = Device2ReleaseDimDown[0].currentLevel - state.dimIncrement
                    Device2ReleaseDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "doubleClick" && buttonNumber == 2) {
                if (Device2DoubleSwitch != null) toggle(Device2DoubleSwitch)
                if (Device2DoubleRoutine != null) location.helloHome?.execute(settings.Device2DoubleRoutine)
                if (Device2DoubleDimUp != null) {
                    def newLevel = Device2DoubleDimUp[0].currentLevel + state.dimIncrement
                    Device2DoubleDimUp.setLevel(newLevel)
                }
                if (Device2DoubleDimDown != null) {
                    def newLevel = Device2DoubleDimDown[0].currentLevel - state.dimIncrement
                    Device2DoubleDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
           	else if (value == "clickHoldStart" && buttonNumber == 2) {
                atomicState.currentButton = buttonNumber
                if (Device2clickholdSwitch != null) toggle(Device2clickholdSwitch)
                if (Device2clickholdRoutine != null) location.helloHome?.execute(settings.Device2clickholdRoutine)
                if (Device2clickholdDimUp != null) {
                    def newLevel = Device2clickholdDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device2clickholdDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber click hold start going up"
                }
                if (Device2clickholdDimDown != null) {
                    def newLevel = Device2clickholdDimDown[0].currentLevel - state.dimIncrement
                    Device2clickholdDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber click hold start going down"
                }
            }
           	else if (value == "clickHoldStop" && buttonNumber == 2) {
                atomicState.currentButton = buttonNumber
                if (Device2clickholdreleaseSwitch != null) toggle(Device2clickholdreleaseSwitch)
                if (Device2clickholdreleaseDimUp != null) {
                    def newLevel = Device2clickholdreleaseDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device2clickholdreleaseDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device2clickholdreleaseDimDown != null) {
                    def newLevel = Device2clickholdreleaseDimDown[0].currentLevel - state.dimIncrement
                    Device2clickholdreleaseDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber long hold going down"
                }
            }
            else if (value == "pushed" && buttonNumber == 3) {
                if (Device3press != null) toggle(Device3press)
                if (Device3pressRoutine != null) location.helloHome?.execute(settings.Device3pressRoutine)
                if (Device3pressDimDown != null) {
                    def newLevel = Device3pressDimDown[0].currentLevel - state.dimIncrement
                    Device3pressDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "held" && buttonNumber == 3) {
                atomicState.currentButton = buttonNumber
                if (Device3longholdSwitch != null) toggle(Device3longholdSwitch)
                if (Device3longholdRoutine != null) location.helloHome?.execute(settings.Device3longholdRoutine)
                if (Device3longholdDimUp != null) {
                    // Device3longholdDimUp.levelUp()
                    def newLevel = Device3longholdDimUp[0].currentLevel + state.dimIncrement
                    Device3longholdDimUp.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device3longholdDimDown != null) {
                    // Device3longholdDimDown.levelDown()
                    def newLevel = Device3longholdDimDown[0].currentLevel - state.dimIncrement
                    Device3longholdDimDown.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going down"
                }
                if (Device3heldDimUp != null) {
                    atomicState.direction = "Up"
                log.debug "Button $buttonNumber Hold Start to go up"
                }
                if (Device3heldDimDown != null) {
                    atomicState.direction = "Down"
                log.debug "Button $buttonNumber Hold Start to go down"
                }
                onButtonHoldStart()
                log.debug "$buttonNumber $value"
            }
            else if (value == "holdRelease" && buttonNumber == 3) {         
                onButtonHoldEnd()
                if (Device3ReleaseSwitch != null) toggle(Device3ReleaseSwitch)
                if (Device3ReleaseDimUp != null) {
                    // Device3ReleaseDimUp.levelUp()
                    def newLevel = Device3ReleaseDimUp[0].currentLevel + state.dimIncrement
                    Device3ReleaseDimUp.setLevel(newLevel)
                }
                if (Device3ReleaseDimDown != null) {
                    // Device3ReleaseDimDown.levelDown()
                    def newLevel = Device3ReleaseDimDown[0].currentLevel - state.dimIncrement
                    Device3ReleaseDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "doubleClick" && buttonNumber == 3) {
                if (Device3DoubleSwitch != null) toggle(Device3DoubleSwitch)
                if (Device3DoubleRoutine != null) location.helloHome?.execute(settings.Device3DoubleRoutine)
                if (Device3DoubleDimUp != null) {
                    def newLevel = Device3DoubleDimUp[0].currentLevel + state.dimIncrement
                    Device3DoubleDimUp.setLevel(newLevel)
                }
                if (Device3DoubleDimDown != null) {
                    def newLevel = Device3DoubleDimDown[0].currentLevel - state.dimIncrement
                    Device3DoubleDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "clickHoldStart" && buttonNumber == 3) {
                atomicState.currentButton = buttonNumber
                if (Device3clickholdSwitch != null) toggle(Device3clickholdSwitch)
                if (Device3clickholdRoutine != null) location.helloHome?.execute(settings.Device3clickholdRoutine)
                if (Device3clickholdDimUp != null) {
                    def newLevel = Device3clickholdDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device3clickholdDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber click hold start going up"
                }
                if (Device3clickholdDimDown != null) {
                    def newLevel = Device3clickholdDimDown[0].currentLevel - state.dimIncrement
                    Device3clickholdDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber click hold start going down"
                }
            }
           	else if (value == "clickHoldStop" && buttonNumber == 3) {
                atomicState.currentButton = buttonNumber
                if (Device3clickholdreleaseSwitch != null) toggle(Device3clickholdreleaseSwitch)
                if (Device3clickholdreleaseDimUp != null) {
                    def newLevel = Device3clickholdreleaseDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device3clickholdreleaseDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device3clickholdreleaseDimDown != null) {
                    def newLevel = Device3clickholdreleaseDimDown[0].currentLevel - state.dimIncrement
                    Device3clickholdreleaseDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber long hold going down"
                }
            }
            else if (value == "pushed" && buttonNumber == 4) {
                if (Device4press != null) toggle(Device4press)
                if (Device4pressRoutine != null) location.helloHome?.execute(settings.Device4pressRoutine)
                if (Device4pressDimDown != null) {
                    def newLevel = Device4pressDimDown[0].currentLevel - state.dimIncrement
                    Device4pressDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "held" && buttonNumber == 4) {
                atomicState.currentButton = buttonNumber
                if (Device4longholdSwitch != null) toggle(Device4longholdSwitch)
                if (Device4longholdRoutine != null) location.helloHome?.execute(settings.Device4longholdRoutine)
                if (Device4longholdDimUp != null) {
                    // Device4longholdDimUp.levelUp()
                    def newLevel = Device4longholdDimUp[0].currentLevel + state.dimIncrement
                    Device4longholdDimUp.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device4longholdDimDown != null) {
                     // Device4longholdDimDown.levelDown()
                     def newLevel = Device4longholdDimDown[0].currentLevel - state.dimIncrement
                    Device4longholdDimDown.setLevel(newLevel)
                log.debug "Button $buttonNumber long hold going down"
                }
                if (Device4heldDimUp != null) {
                    atomicState.direction = "Up"
                log.debug "Button $buttonNumber Hold Start to go up"
                }
                if (Device4heldDimDown != null) {
                    atomicState.direction = "Down"
                log.debug "Button $buttonNumber Hold Start to go down"
                }
                onButtonHoldStart()
                log.debug "$buttonNumber $value"
            }
            else if (value == "holdRelease" && buttonNumber == 4) {         
                onButtonHoldEnd()
                if (Device4ReleaseSwitch != null) toggle(Device4ReleaseSwitch)
                if (Device4ReleaseDimUp != null) {
                     Device4ReleaseDimUp.levelUp()
                    def newLevel = Device4ReleaseDimUp[0].currentLevel + state.dimIncrement
                    Device4ReleaseDimUp.setLevel(newLevel)
                }
                if (Device4ReleaseDimDown != null) {
                     Device4ReleaseDimDown.levelDown()
                    def newLevel = Device4ReleaseDimDown[0].currentLevel - state.dimIncrement
                    Device4ReleaseDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
            else if (value == "doubleClick" && buttonNumber == 4) {
                if (Device4DoubleSwitch != null) toggle(Device4DoubleSwitch)
                if (Device4DoubleRoutine != null) location.helloHome?.execute(settings.Device4DoubleRoutine)
                if (Device4DoubleDimUp != null) {
                    def newLevel = Device4DoubleDimUp[0].currentLevel + state.dimIncrement
                    Device4DoubleDimUp.setLevel(newLevel)
                }
                if (Device4DoubleDimDown != null) {
                    def newLevel = Device4DoubleDimDown[0].currentLevel - state.dimIncrement
                    Device4DoubleDimDown.setLevel(newLevel)
                }
                log.debug "$buttonNumber $value"
            }
           	else if (value == "clickHoldStart" && buttonNumber == 4) {
                atomicState.currentButton = buttonNumber
                if (Device4clickholdSwitch != null) toggle(Device4clickholdSwitch)
                if (Device4clickholdRoutine != null) location.helloHome?.execute(settings.Device4clickholdRoutine)
                if (Device4clickholdDimUp != null) {
                    def newLevel = Device4clickholdDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device4clickholdDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber click hold start going up"
                }
                if (Device4clickholdDimDown != null) {
                    def newLevel = Device4clickholdDimDown[0].currentLevel - state.dimIncrement
                    Device4clickholdDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber click hold start going down"
                }
            }
           	else if (value == "clickHoldStop" && buttonNumber == 4) {
                atomicState.currentButton = buttonNumber
                if (Device4clickholdreleaseSwitch != null) toggle(Device4clickholdreleaseSwitch)
                if (Device4clickholdreleaseDimUp != null) {
                    def newLevel = Device4clickholdreleaseDimUp[0].currentLevel + state.dimIncrement
                    log.debug("newLevel is $newLevel")
                    Device4clickholdreleaseDimUp.setLevel(newLevel)
                    // Device1longholdDimUp.levelUp()
                log.debug "Button $buttonNumber long hold going up"
                }
                if (Device4clickholdreleaseDimDown != null) {
                    def newLevel = Device4clickholdreleaseDimDown[0].currentLevel - state.dimIncrement
                    Device4clickholdreleaseDimDown.setLevel(newLevel)
                    // Device1longholdDimDown.levelDown()
                log.debug "Button $buttonNumber long hold going down"
                }
            }

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
// execution filter methods
private getAllOk() {
    modeOk && daysOk && timeOk
}

private getModeOk() {
    def result = !modes || modes.contains(location.mode)
//  log.trace "modeOk = $result"
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
//  log.trace "daysOk = $result"
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
//  log.trace "timeOk = $result"
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
