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
	definition (name: "Z-Wave Switch Secure TKB TZ69", namespace: "TKB", author: "AdamV") {
		capability "Switch"
		capability "Refresh"
		capability "Polling"
		capability "Actuator"
		capability "Sensor"
        capability "Power Meter"
		capability "Energy Meter"
        capability "Configuration"

		fingerprint inClusters: "0x25,0x98"
		fingerprint deviceId: "0x10", inClusters: "0x98"
	}

	simulator {
		status "on":  "command: 9881, payload: 002503FF"
		status "off": "command: 9881, payload: 00250300"

		reply "9881002001FF,delay 200,9881002502": "command: 9881, payload: 002503FF"
		reply "988100200100,delay 200,9881002502": "command: 9881, payload: 00250300"
	}


   tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: 'Turning On', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821", nextState: "turningOff"
				attributeState "turningOff", label: 'Turning Off', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			}
			tileAttribute ("power", key: "SECONDARY_CONTROL") {
				attributeState "power", label:'${currentValue} W'
			}
		} 
    
		/*standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}*/
		standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "configure", label:'configure', action:"configuration.configure", icon:"st.secondary.configure"
		}
        

		main "switch"
		details(["switch","refresh","configure"])
	}
}

def updated() {
	response(refresh())
}

def parse(String description) {
	def results = []
  //   log.debug("RAW command: $description")
	if (description.startsWith("Err")) {
		log.debug("An error has occurred")
		} 
    else {
       
       	def cmd = zwave.parse(description.replace("98C1", "9881"), [0x98: 1, 0x20: 1, 0x84: 1, 0x80: 1, 0x60: 3, 0x2B: 1, 0x26: 1])
        // log.debug "Parsed Command: $cmd"
        if (cmd) {
       	results = zwaveEvent(cmd)
       // log.debug("RAW command: $cmd")
		}
    }
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
	createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "physical")
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "physical")
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd) {
	createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
}

def zwaveEvent(physicalgraph.zwave.commands.hailv1.Hail cmd) {
	createEvent(name: "hail", value: "hail", descriptionText: "Switch button was pressed", displayed: false)
}

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
	def encapsulatedCommand = cmd.encapsulatedCommand([0x20: 1, 0x25: 1])
	if (encapsulatedCommand) {
		state.sec = 1
		zwaveEvent(encapsulatedCommand)
        //log.debug("encap command command: $encapsulatedCommand")
	}
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
	log.debug "Unhandled: $cmd"
	null
}

def on() {
	commands([
		zwave.basicV1.basicSet(value: 0xFF),
        zwave.configurationV2.configurationSet(configurationValue: [0, 5], parameterNumber: 1, size: 2),
		zwave.switchBinaryV1.switchBinaryGet()
	])
}

def off() {
	commands([
		zwave.basicV1.basicSet(value: 0x00),
        zwave.configurationV2.configurationSet(configurationValue: [0, 255], parameterNumber: 1, size: 2),
		zwave.switchBinaryV1.switchBinaryGet()
	])
}

def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	
	def powerValue = cmd.scaledMeterValue
	sendEvent(name: "power", value: powerValue, descriptionText: '{{ device.displayName }} power is {{ value }} Watts', translatable: true )
    log.debug("power: $powerValue Watts")

}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
	
    if (cmd.size == 2){
    	def valX1 = cmd.configurationValue[0]
       	def valY1 = cmd.configurationValue[1]
       	def diff = binaryToDegrees(valX1, valY1)
        log.debug("param: $cmd.parameterNumber, value: $diff")
    }
    else{
		log.debug("param: $cmd.parameterNumber, value: $cmd.configurationValue")
    }
}

def poll() {
	refresh()
}

def refresh() {
	commands([
		zwave.switchBinaryV1.switchBinaryGet(),
        zwave.meterV3.meterGet(scale: 2),
        zwave.configurationV2.configurationGet(parameterNumber: 1),
        zwave.configurationV2.configurationGet(parameterNumber: 2),
        zwave.configurationV2.configurationGet(parameterNumber: 3),
        zwave.configurationV2.configurationGet(parameterNumber: 4),
        zwave.configurationV2.configurationGet(parameterNumber: 5),
        zwave.configurationV2.configurationGet(parameterNumber: 6),
        zwave.configurationV2.configurationGet(parameterNumber: 7),
        zwave.configurationV2.configurationGet(parameterNumber: 8),
        zwave.configurationV2.configurationGet(parameterNumber: 9)
	])
}

def configure() {
	commands([
		zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId),
        zwave.configurationV2.configurationSet(configurationValue: [0, 255], parameterNumber: 1, size: 2)
        //zwave.configurationV2.configurationSet(configurationValue: [0], parameterNumber: 7, size: 1),
        
    ])
    
  /*  def cmds = []
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 1, size: 2, configurationValue: [0,200]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 6, size: 1, configurationValue: [0]).format()
    
    if ( cmds != [] && cmds != null ) return delayBetween(cmds, 500) else return
   */ 
    
}
/*  def refresh() {
    
 
    def commands = [ ]
	def cmds = []
    cmds << zwave.switchBinaryV1.switchBinaryGet()
    cmds << zwave.meterV3.meterGet()
    
    delayBetween(cmds, 500)
}
*/

def binaryToDegrees(x, y) {
	def degrees = 0
    def preDegrees = 0
    
    degrees = y + (x*256)
    
    
    return degrees
}

private command(physicalgraph.zwave.Command cmd) {
	if (state.sec != 0) {
		zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
	} else {
		cmd.format()
	}
}

private commands(commands, delay=1000) {
	delayBetween(commands.collect{ command(it) }, delay)
}
