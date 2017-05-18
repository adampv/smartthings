/**
 *  Copyright 2017 AdamV
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
 *  Version 0.1
 *  Author: AdamV
 *  Date: 2017-04-02
 *
 */
 
metadata {
	definition (name: "Flush DC Shutter", namespace: "Qubino", author: "AdamV") {
		capability "Actuator"
		capability "Configuration" 
       	capability "Refresh"
        capability "Power Meter"
		capability "Energy Meter"
        capability "Switch Level"
        capability "Switch"
		capability "Polling"
		capability "Sensor"
		capability "Actuator"

		
        
		attribute "numberOfButtons", "number"
        attribute "Button Events", "enum",  ["#1 pushed", "#1 held", "#1 double clicked", "#1 click held", "#1 hold released", "#1 click hold released", "#2 pushed", "#2 held", "#2 double clicked", "#2 click held", "#2 hold released", "#2 click hold released", "#3 pushed", "#3 held", "#3 double clicked", "#3 click held", "#3 hold released", "#3 click hold released", "#4 pushed", "#4 held", "#4 double clicked", "#4 click held", "#4 hold released", "#4 click hold released"]
        attribute "button", "enum", ["pushed", "held", "double clicked", "click held"]
        
                // Custom Commands:
        command "describeAttributes"
        command "autoCalStart"
        command "autoCalStop"
        
		fingerprint deviceId: "0x1107", inClusters: "0x5E, 0x86, 0x72, 0x5A, 0x73, 0x20, 0x27, 0x25, 0x26, 0x32, 0x85, 0x8E, 0x59, 0x70", outClusters: "0x20, 0x26"											
   
   }

	simulator {
		status "button 1 pushed":  "command: 9881, payload: 00 5B 03 DE 00 01"
		
        // need to redo simulator commands

	}
    tiles (scale: 2){
		
		multiAttributeTile(name:"button", type:"generic", width:6, height:4) {
    		tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
        		attributeState "on", action:"switch.off", label:'Blinds Drawn', icon: 'st.doors.garage.garage-closed', backgroundColor:"#00A0DC", nextState:"turningOff"
        		attributeState "off", action:"switch.on", label:'Binds Open', backgroundColor:"#ffffff", icon: 'st.doors.garage.garage-open', nextState:"turningOn"
        		attributeState "turningOn", label:'Drawing Binds',  icon:'st.doors.garage.garage-closing', backgroundColor:"#79b821", nextState:"turningOff"
        		attributeState "turningOff", label:'Opening Blinds', icon:'st.doors.garage.garage-opening', backgroundColor:"#ffffff", nextState:"turningOn"
    		}
            tileAttribute("device.level", key: "SLIDER_CONTROL") {
        		attributeState "level", action:"switch level.setLevel", defaultState: true
    		}
			tileAttribute ("power", key: "SECONDARY_CONTROL") {
				attributeState "power", icon: 'st.Weather.weather1', label:'${currentValue} W'
			}

	}
	/*tiles {
		standardTile("button", "device.button", width: 2, height: 2) {
			state "default", label: "", icon: "st.Home.home30", backgroundColor: "#ffffff"
            state "held", label: "holding", icon: "st.Home.home30", backgroundColor: "#C390D4"
        }
    	 valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat") {
         	tileAttribute ("device.battery", key: "PRIMARY_CONTROL"){
                        state "battery", label:'${currentValue}% battery', unit:""
        	}
        }*/
      /*  standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
			state "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			state "turningOn", label:'${name}', icon:"st.switches.switch.on", backgroundColor:"#79b821"
			state "turningOff", label:'${name}', icon:"st.switches.switch.off", backgroundColor:"#ffffff"
		}*/
        standardTile("StartAutoCal", "device.configure", width: 2, height: 2, decoration: "flat") {
			state "default", label: "Start Auto Cal", icon:"st.Transportation.transportation13", backgroundColor: "#ffffff", action: "autoCalStart"
        }
        standardTile("StopAutoCal", "device.configure", width: 2, height: 2, decoration: "flat") {
			state "default", label: "Stop Auto Cal", icon:"st.Transportation.transportation13", backgroundColor: "#ffffff", action: "autoCalStop"
        }
        standardTile("configure", "device.configure", width: 2, height: 2, decoration: "flat") {
			state "default", icon:"st.secondary.configure", backgroundColor: "#ffffff", action: "configuration.configure"
        }
        /*controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 3, inactiveLabel: false) {
		state "level", action:"switch level.setLevel"
		}*/
        valueTile("power", "device.power", width: 2, height: 2, decoration: "flat") {
		state "default", label:'${currentValue} W'
		}
		valueTile("energy", "device.energy", width: 2, height: 2, decoration: "flat") {
		state "default", label:'${currentValue} kWh'
		}
		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'reset kWh', action:"reset"
		}
       	standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
		state "default", action:"polling.poll", icon:"st.secondary.refresh"
		}
        standardTile("blank", "device.thermostatMode", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
		state "default", action:"", icon:""
		}
        
        main "button"
		details(["button", "power", "energy", "reset",  "configure", "refresh", "blank", "StartAutoCal", "StopAutoCal"])
	}
    
}

def parse(String description) {
	def results = []
    // log.debug("RAW command: $description")
	if (description.startsWith("Err")) {
		log.debug("An error has occurred")
		} 
    else {
       
       	def cmd = zwave.parse(description.replace("98C1", "9881"), [0x98: 1, 0x20: 1, 0x84: 1, 0x80: 1, 0x60: 3, 0x2B: 1, 0x26: 1])
        // log.debug "Parsed Command: $cmd"
        if (cmd) {
       	results = zwaveEvent(cmd)
		}
    }
}

/*
def parse(String description) {
	def results = []
  //   log.debug("RAW command: $description")
	if (description.startsWith("Err")) {
		log.debug("An error has occurred")
		} 
    else {
       
       	def cmd = zwave.parse(description.replace("98C1", "9881"), [0x98: 1, 0x20: 1, 0x84: 1, 0x80: 1, 0x60: 3, 0x2B: 1, 0x26: 1])
        log.debug "Parsed Command: $cmd"
        if (cmd) {
       	results = zwaveEvent(cmd)
		}
        if ( !state.numberOfButtons ) {
    	state.numberOfButtons = "8"
        createEvent(name: "numberOfButtons", value: "8", displayed: false)

  		}
    }
}
*/ 
def describeAttributes(payload) {
    	payload.attributes = [
        [ name: "holdLevel",    type: "number",    range:"1..100", capability: "button" ],
       	[ name: "Button Events",    type: "enum",    options: ["#1 pushed", "#1 held", "#1 double clicked", "#1 click held", "#1 hold released", "#1 click hold released", "#2 pushed", "#2 held", "#2 double clicked", "#2 click held", "#2 hold released", "#2 click hold released", "#3 pushed", "#3 held", "#3 double clicked", "#3 click held", "#3 hold released", "#3 click hold released", "#4 pushed", "#4 held", "#4 double clicked", "#4 click held", "#4 hold released", "#4 click hold released"], momentary: true ],
    	[ name: "button",    type: "enum",    options: ["pushed", "held", "double clicked", "click held"],  capability: "button", momentary: true ],
        ]
    	return null
		}	
        

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
        def encapsulatedCommand = cmd.encapsulatedCommand([0x98: 1, 0x20: 1])
			//	log.debug("UnsecuredCommand: $encapsulatedCommand")
        // can specify command class versions here like in zwave.parse
        if (encapsulatedCommand) {
       // 	log.debug("UnsecuredCommand: $encapsulatedCommand")
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

def setLevel(level) {
	if(level > 99) level = 99
		delayBetween([
		//zwave.basicV1.basicSet(value: level).format(),
        zwave.switchMultilevelV3.switchMultilevelSet(value: level).format(),
       // zwave.switchmultilevelV1.SwitchMultilevelSet(value: level).format(),
		zwave.switchMultilevelV1.switchMultilevelGet().format()
		], 5000) //Orignal value 5000
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelGet cmd) {
	log.debug "Multilevel get: $cmd"
}

def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelSet cmd) {
	log.debug "Multilevel set: $cmd"
    if (cmd.value == 0){
    sendEvent(name: 'switch', value: "off" as String)
    sendEvent(name: 'level', value: cmd.value)
    }
    if (cmd.value >= 1){
    sendEvent(name: 'switch', value: "on" as String)
    sendEvent(name: 'level', value: cmd.value)
    }
}

def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
		log.debug "Meter report: $cmd"
        if (cmd.meterType == 1) {
		if (cmd.scale == 0) {
			return createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
		} else if (cmd.scale == 1) {
			return createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
		} else if (cmd.scale == 2) {
			return createEvent(name: "power", value: Math.round(cmd.scaledMeterValue), unit: "W")
		} else {
			return createEvent(name: "electric", value: cmd.scaledMeterValue, unit: ["pulses", "V", "A", "R/Z", ""][cmd.scale - 3])
			}
		}
}
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv1.SwitchMultilevelReport cmd) {
	log.debug "Multilevel report: $cmd"
    if (cmd.value == 0){
    sendEvent(name: 'switch', value: "off" as String)
    sendEvent(name: 'level', value: cmd.value)
    }
    if (cmd.value >= 1){
    sendEvent(name: 'switch', value: "on" as String)
    sendEvent(name: 'level', value: cmd.value)
    }
    //dimmerEvents(cmd)
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
        sendEvent(map)
}


def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd){
        log.debug "basic event: $cmd.value"
       // dimmerEvents(cmd)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
		log.debug "basic set event: $cmd.value"
		//dimmerEvents(cmd)
}
/*
def dimmerEvents(physicalgraph.zwave.Command cmd) {
def result = []
def value = (cmd.value ? "on" : "off")
def switchEvent = createEvent(name: "switch", value: value, descriptionText: "$device.displayName was turned $value")
result << switchEvent
if (cmd.value) {
result << createEvent(name: "level", value: cmd.value, unit: "%")
}
if (switchEvent.isStateChange) {
result << response(["delay 3000", zwave.meterV2.meterGet(scale: 2).format()])
} //Orignal value 3000
return result
}
*/
def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
	// log.debug( "Dimming Duration: $cmd.dimmingDuration")
    // log.debug( "Button code: $cmd.sceneId")
   
    

}
 
def refresh() {
	configure()
}

  def configure() {
    
 
    def commands = [ ]
			log.debug "Resetting Sensor Parameters to SmartThings Compatible Defaults"
	def cmds = []
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 1, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 2, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 3, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 4, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 5, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 6, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 7, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 8, nodeId: zwaveHubNodeId).format()
    cmds << zwave.associationV1.associationSet(groupingIdentifier: 9, nodeId: zwaveHubNodeId).format()
	cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 71, size: 1).format()
	cmds << zwave.configurationV1.configurationSet(configurationValue: [8], parameterNumber: 40, size: 1).format() // power reporting step %
    cmds << zwave.configurationV1.configurationSet(configurationValue: [0,255], parameterNumber: 10, size: 2).format()
    
    delayBetween(cmds, 500)
}

  def autoCalStart() {
    
 
    def commands = [ ]
			log.debug "Starting AutoCal"
	def cmds = []
	cmds << zwave.configurationV1.configurationSet(configurationValue: [1], parameterNumber: 78, size: 1).format()

    delayBetween(cmds, 500)
}

  def autoCalStop() {
    
 
    def commands = [ ]
			log.debug "Stopping AutoCal"
	def cmds = []
	cmds << zwave.configurationV1.configurationSet(configurationValue: [0], parameterNumber: 78, size: 1).format()

    delayBetween(cmds, 500)
}

def on() {
	delayBetween([
	zwave.switchMultilevelV3.switchMultilevelSet(value: 99).format(),
  //  zwave.switchMultilevelV3.switchMultilevelSet(value: 99).format(),
  //  zwave.basicV1.basicSet(value: 0xFF).format(),
	zwave.switchMultilevelV1.switchMultilevelGet().format(),
	], 1250) //Orignal value 5000
}

def off() {
	delayBetween([
	zwave.switchMultilevelV3.switchMultilevelSet(value: 0).format(),
   // zwave.switchMultilevelV3.switchMultilevelSet(value: 0).format(),
    //zwave.basicV1.basicSet(value: 0x00).format(),
	zwave.switchMultilevelV1.switchMultilevelGet().format(),
	], 1250) //Orignal value 5000
}

def toTwoBytes(x, y) {
	def degrees = 0
    def preDegrees = 0
    if (x == 0){
    	degrees = y / 10
    }
    else if (x == 1){
    	preDegrees = y + 256
        degrees = preDegrees / 10
    }
    
    return degrees
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
log.debug("config reprt: $cmd")
}

def poll() {
	delayBetween([
		zwave.sensorMultilevelV5.sensorMultilevelGet().format(), // current temperature
        zwave.switchMultilevelV3.switchMultilevelGet().format(),
        zwave.meterV3.meterGet().format(),
        zwave.configurationV1.configurationGet(parameterNumber: 10).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 40).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 42).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 71).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 72).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 73).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 74).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 76).format(),
       // zwave.configurationV1.configurationGet(parameterNumber: 78).format(),
        zwave.configurationV1.configurationGet(parameterNumber: 85).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 86).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 90).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 110).format(),
        //zwave.configurationV1.configurationGet(parameterNumber: 120).format()
        
        // zwave.basicV1.basicGet().format(),
		// zwave.thermostatOperatingStateV2.thermostatOperatingStateGet().format()
	], 750)
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
