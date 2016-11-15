/**
 *  Copyright 2016 AdamV
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
 */
metadata {
	definition (name: "Smart Color Button PSR04", namespace: "Philio", author: "AdamV") {
		capability "Actuator"
		capability "Switch"
		capability "Switch Level"
		capability "Refresh"
		capability "Sensor"
        capability "Configuration"
        capability "Battery"

		fingerprint deviceId: "0x1801", inClusters: "0x5E, 0x80, 0x85, 0x70, 0x72, 0x86, 0x84, 0x59, 0x73, 0x5A, 0x8F, 0x98, 0x7A, 0x5B", outClusters: "0x20"
									
    }

	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"
	}

	tiles {
    
    	multiAttributeTile(name:"button", type:"lighting", width:6, height:4) {
  			tileAttribute("device.button", key: "PRIMARY_CONTROL"){
    		attributeState "default", label:'Controller', backgroundColor:"#44b621", icon:"st.Home.home30"
            attributeState "held", label: "holding", backgroundColor: "#C390D4"
  			}
            tileAttribute ("device.battery", key: "SECONDARY_CONTROL") {
			attributeState "battery", label:'${currentValue} % battery'
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
           // tileAttribute ("device.level", key: "SECONDARY_CONTROL") {
           // attributeState "level", label: 'Level is ${currentValue}%'
            //}
        }

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
       	valueTile("configure", "device.button", width: 2, height: 2, decoration: "flat") {
			state "default", label: "configure", backgroundColor: "#ffffff", action: "configure"
        }

		main "button"
		details (["button", "refresh", "configure"])
	}
}

def parse(String description) {
	def result = []
	if (description.startsWith("Err")) {
	    result = createEvent(descriptionText:description, isStateChange:true)
	} else {
		def cmd = zwave.parse(description, [0x20: 1, 0x84: 1, 0x98: 1, 0x56: 1, 0x60: 3])
		if (cmd) {
			result += zwaveEvent(cmd)
		}
        log.debug("This is what I receive: $cmd")
	}
	return result
}

def updated() {
	response(zwave.wakeUpV1.wakeUpNoMoreInformation())
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
	[ createEvent(descriptionText: "${device.displayName} woke up"),
	  response(zwave.wakeUpV1.wakeUpNoMoreInformation()) ]
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	if (cmd.value == 0) {
		createEvent(name: "switch", value: "off")
	} else if (cmd.value == 255) {
		createEvent(name: "switch", value: "on")
	} else {
		[ createEvent(name: "switch", value: "on"), createEvent(name: "switchLevel", value: cmd.value) ]
	}
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

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x20: 1, 0x84: 1])
	if (encapsulatedCommand) {
		state.sec = 1
		def result = zwaveEvent(encapsulatedCommand)
		result = result.collect {
			if (it instanceof physicalgraph.device.HubAction && !it.toString().startsWith("9881")) {
				response(cmd.CMD + "00" + it.toString())
			} else {
				it
			}
		}
		result
	}
}

def zwaveEvent(physicalgraph.zwave.commands.centralscenev1.CentralSceneNotification cmd) {
		log.debug( "keyAttributes: $cmd.keyAttributes")
        log.debug( "sceneNumber: $cmd.sceneNumber")
        log.debug( "sequenceNumber: $cmd.sequenceNumber")
        if ( cmd.sceneNumber == 1 ) {
        	Integer button = 1
            sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
            log.debug( "Button $button was pushed" )
            }
      // 	log.debug( "payload: $cmd.payload")
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
		log.debug( "Value: $cmd.value")
        def dimmerValue = cmd.value
   	if (cmd.value == 0) {
		sendEvent(name: "switch", value: "off")
        log.debug("turned off")
        sendEvent(name: "level", value: dimmerValue)
	} else if (cmd.value == 255) {
		sendEvent(name: "switch", value: "on")
        log.debug("turned on")
	} else {
		sendEvent(name: "switch", value: "on")
        //createEvent(name: "level", value: dimmerValue)
        sendEvent(name: "level", value: dimmerValue)
        log.debug("sent switch on at value $cmd.value")
	}

      // 	log.debug( "payload: $cmd.payload")
}

 

def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
	def versions = [0x31: 2, 0x30: 1, 0x84: 1, 0x9C: 1, 0x70: 2]
	// def encapsulatedCommand = cmd.encapsulatedCommand(versions)
	def version = versions[cmd.commandClass as Integer]
	def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
	def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand)
	}
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	createEvent(descriptionText: "$device.displayName: $cmd", isStateChange: true)
}

def on() {
	commands([zwave.basicV1.basicSet(value: 0xFF), zwave.basicV1.basicGet()])
}

def off() {
	commands([zwave.basicV1.basicSet(value: 0x00), zwave.basicV1.basicGet()])
}

def refresh() {
	command(zwave.basicV1.basicGet())
}

def setLevel(value) {
	commands([zwave.basicV1.basicSet(value: value as Integer), zwave.basicV1.basicGet()], 4000)
}

private command(physicalgraph.zwave.Command cmd) {
	if (state.sec) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private commands(commands, delay=200) {
	delayBetween(commands.collect{ command(it) }, delay)
}

  def configure() {
    
 
    def commands = [ ]
			log.debug "Resetting Sensor Parameters to SmartThings Compatible Defaults"
	def cmds = []
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 1, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 2, nodeId: zwaveHubNodeId).format()
    cmds << zwave.configurationV1.configurationSet(configurationValue: [10], parameterNumber: 10, size: 1).format()
    
    delayBetween(cmds, 500)
}
