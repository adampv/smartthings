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
 * 	
 *
 */
 
metadata {
	definition (name: "Secure Wall Controllers & Key Fobs (Z-Wave.me, Popp & Devolo)", namespace: "Z-Wave.me", author: "AdamV") {
		capability "Actuator"
		capability "Button"
        capability "Battery"
		capability "Configuration" 
       	capability "Refresh"

        
		fingerprint deviceId: "0x1801", inClusters: "0x5E, 0x70, 0x85, 0x2D, 0x8E, 0x80, 0x84, 0x8F, 0x5A, 0x59, 0x5B, 0x73, 0x86, 0x72, 0xEF, 0x20, 0x5B, 0x26, 0x27, 0x2B, 0x60"
   		fingerprint deviceId: "0x1202", inClusters: "0x5E, 0x8F, 0x73, 0x98, 0x86, 0x72, 0x70, 0x85, 0x2D, 0x8E, 0x80, 0x84, 0x5A, 0x59, 0x5B, 0xEF, 0x20, 0x5B, 0x26, 0x27, 0x2B, 0x60"												
   }

	simulator {
		status "button 1 pushed":  "command: 9881, payload: 00 5B 03 DE 00 01"
		
        // need to redo simulator commands

	}
	tiles {
		standardTile("button", "device.button", width: 2, height: 2) {
			state "default", label: "", icon: "st.Home.home30", backgroundColor: "#ffffff"
            state "held", label: "holding", icon: "st.Home.home30", backgroundColor: "#C390D4"
        }
    	 valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
         	tileAttribute ("device.battery", key: "PRIMARY_CONTROL"){
                        state "battery", label:'${currentValue}% battery', unit:""
        	}
        }
        standardTile("configure", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "configure", backgroundColor: "#ffffff", action: "configure"
        }
        
        main "button"
		details(["button", "battery", "configure"])
	}
    
}

def parse(String description) {
	def results = []
    log.debug("RAW command: $description")
	if (description.startsWith("Err")) {
		log.debug("An error has occurred")
		} 
    else {
       
       	def cmd = zwave.parse(description.replace("98C1", "9881"), [0x98: 1, 0x20: 1, 0x84: 1, 0x80: 1, 0x60: 3, 0x2B: 1, 0x26: 1])
    //    log.debug "Parsed Command: $cmd"
        if (cmd) {
       	results = zwaveEvent(cmd)
		}
    }
}
  

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
        def encapsulatedCommand = cmd.encapsulatedCommand([0x98: 1, 0x20: 1])
				log.debug("UnsecuredCommand: $encapsulatedCommand")
        // can specify command class versions here like in zwave.parse
        if (encapsulatedCommand) {
        		log.debug("UnsecuredCommand: $encapsulatedCommand")
                return zwaveEvent(encapsulatedCommand)
        }
}

def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
		log.debug( "keyAttributes: $cmd.keyAttributes")
        log.debug( "sceneNumber: $cmd.sceneNumber")
        log.debug( "sequenceNumber: $cmd.sequenceNumber")
      // 	log.debug( "payload: $cmd.payload")
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	[ createEvent(descriptionText: "${device.displayName} woke up"),
	  response(zwave.wakeUpV1.wakeUpNoMoreInformation()) ]
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelGet cmd) {
	log.debug "Multilevel get: $cmd"
}
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd) {
	log.debug "Multilevel report: $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
        def map = [ name: "battery", unit: "%" ]
        if (cmd.batteryLevel == 0xFF) {  // Special value for low battery alert
                map.value = 1
                map.descriptionText = "${device.displayName} has a low battery"
                map.isStateChange = true
        } else {
                map.value = cmd.batteryLevel
                log.debug ("Battery: $cmd.batteryLevel")
        }
        // Store time of last battery update so we don't ask every wakeup, see WakeUpNotification handler
        state.lastbatt = new Date().time
        createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd){
        log.debug "basic event: $cmd.value"
}


def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	// log.debug( "Dimming Duration: $cmd.dimmingDuration")
    // log.debug( "Button code: $cmd.sceneId")
   
    
    if ( cmd.sceneId == 11 ) {
        	Integer button = 1
            sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
        	log.debug( "Button $button was pushed" )
            }
   	else if  ( cmd.sceneId == 12 ) {
			Integer button = 1
            def patchButton = button + 4
			sendEvent(name: "button", value: "doubleClick", data: [buttonNumber: button], descriptionText: "$device.displayName Button $button was Double Clicked", isStateChange: true)
			sendEvent(name: "button", value: "pushed", data: [buttonNumber: patchButton], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
            log.debug( "Button $button was Double Clicked" )
            }
   	else if  ( cmd.sceneId == 13 ) {
        	Integer button = 1
            sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "Button $button is closed", isStateChange: true)
            log.debug( "Button $button Hold start - held" )
            }
   	else if  ( cmd.sceneId == 15 ) {
        	Integer button = 1
            sendEvent(name: "button", value: "holdRelease", data: [buttonNumber: button], descriptionText: "Button $button is open")
        	log.debug( "Button $button Hold stop" )
            }
    else if  ( cmd.sceneId == 21 ) {
        	Integer button = 2
            sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
        	log.debug( "Button $button was pushed" )
            }
    else if  ( cmd.sceneId == 22 ) {
			Integer button = 2
            def patchButton = button + 4
			sendEvent(name: "button", value: "doubleClick", data: [buttonNumber: button], descriptionText: "$device.displayName Button $button was Double Clicked", isStateChange: true)
			sendEvent(name: "button", value: "pushed", data: [buttonNumber: patchButton], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
            log.debug( "Button $button was Double Clicked" )
            }
    else if  ( cmd.sceneId == 23 ) {
        	Integer button = 2
            sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "Button $button is closed")
        	log.debug( "Button $button Hold start - held" )
            }
   	else if  ( cmd.sceneId == 25 ) {
        	Integer button = 2
            sendEvent(name: "button", value: "holdRelease", data: [buttonNumber: button], descriptionText: "Button $button is open")
        	log.debug( "Button $button Hold stop" )
            }
	else if  ( cmd.sceneId == 31 ) {
        	Integer button = 3
            sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
        	log.debug( "Button $button was pushed" )
            }
    else if  ( cmd.sceneId == 32 ) {
			Integer button = 3
            def patchButton = button + 4
			sendEvent(name: "button", value: "doubleClick", data: [buttonNumber: button], descriptionText: "$device.displayName Button $button was Double Clicked", isStateChange: true)
			sendEvent(name: "button", value: "pushed", data: [buttonNumber: patchButton], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
            log.debug( "Button $button was Double Clicked" )
            }
    else if  ( cmd.sceneId == 33 ) {
        	Integer button = 3
            sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "Button $button is closed")
        	log.debug( "Button $button Hold start - held" )
            }
   	else if  ( cmd.sceneId == 35 ) {
        	Integer button = 3
            sendEvent(name: "button", value: "holdRelease", data: [buttonNumber: button], descriptionText: "Button $button is open")
        	log.debug( "Button $button Hold stop" )
            }
    else if ( cmd.sceneId == 41 ) {
        	Integer button = 4
            sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
        	log.debug( "Button $button was pushed" )
            }
    else if  ( cmd.sceneId == 42 ) {
			Integer button = 4
            def patchButton = button + 4
			sendEvent(name: "button", value: "doubleClick", data: [buttonNumber: button], descriptionText: "$device.displayName Button $button was Double Clicked", isStateChange: true)
			sendEvent(name: "button", value: "pushed", data: [buttonNumber: patchButton], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
            log.debug( "Button $button was Double Clicked" )
            }
    else if  ( cmd.sceneId == 43 ) {
        	Integer button = 4
            sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "Button $button is closed")
        	log.debug( "Button $button Hold start - held" )
            }
   	else if  ( cmd.sceneId == 45 ) {
        	Integer button = 4
            sendEvent(name: "button", value: "holdRelease", data: [buttonNumber: button], descriptionText: "Button $button is open")
        	log.debug( "Button $button Hold stop" )
            }
    else {
        	log.debug( "Commands and Button ID combinations unaccounted for happened" )
            }
}
 

/*
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	[ descriptionText: "$device.displayName: $cmd", linkText:device.displayName, displayed: false ]
	log.debug "command event: $cmd"
}
*/



  def configure() {
    
    
    
    
    def commands = [ ]
			log.debug "Resetting Sensor Parameters to SmartThings Compatible Defaults"
	def cmds = []
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 2, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 3, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 4, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 5, nodeId: zwaveHubNodeId).format()
	cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 1, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 2, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [4], parameterNumber: 11, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [4], parameterNumber: 12, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [4], parameterNumber: 13, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [4], parameterNumber: 14, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 21, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 22, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [2], parameterNumber: 24, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 25, size: 1).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 30, size: 1).format()
    
    delayBetween(cmds, 500)
}
       


/*
// Correct configure for dim events:

    for (def i = 11; i <= 12; i++) {
      	commands << zwave.associationV1.associationSet(groupingIdentifier: 2, nodeId: zwaveHubNodeId).format()
   // 	commands << zwave.sceneControllerConfV1.sceneControllerConfSet(groupId: 4, sceneId:i).format()
       commands << zwave.configurationV1.configurationSet(parameterNumber:i, size: 1, scaledConfigurationValue:4).format()
	}
        for (def i = 13; i <= 14; i++) {
      	commands << zwave.associationV1.associationSet(groupingIdentifier: 3, nodeId: zwaveHubNodeId).format()
    //	commands << zwave.sceneControllerConfV1.sceneControllerConfSet(groupId: 5, sceneId:i).format()
       commands << zwave.configurationV1.configurationSet(parameterNumber:i, size: 1, scaledConfigurationValue:4).format()
	}
	
    log.debug("Sending configuration")
	
    
    delayBetween(commands, 1250)
    
 */
