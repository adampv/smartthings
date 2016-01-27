/**
 *  Lifx Http
 *
 *  Copyright 2014 AdamV
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
 * Lot's of credit to Nicolas Cerveaux - I used his code as a base
 */

metadata {
    definition (name: "LIFX Group version", namespace: "lifx", author: "AdamV") {
        capability "Polling"
        capability "Switch"
        capability "Switch Level"
        capability "Color Control"
        capability "Refresh"
		    capability "Color Temperature"
		    capability "Actuator"
        capability "Sensor"
        
        command "setAdjustedColor"
        command "setColor"
        
        attribute "colorName", "string"
    }

    simulator {
    }

	tiles (scale: 2){
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"http://hosted.lifx.co/smartthings/v1/196xOn.png", backgroundColor:"#fffA62", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"http://hosted.lifx.co/smartthings/v1/196xOff.png", backgroundColor:"#fffA62", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setAdjustedColor"
			}
			tileAttribute ("device.group", key: "SECONDARY_CONTROL") {
				attributeState "group", label: '${currentValue}'
			}
		}  
 /*       standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
            state "on", label:'${name}', action:"switch.off", icon:"st.Lighting.light14", backgroundColor:"#79b821", nextState:"turningOff"
            state "off", label:'${name}', action:"switch.on", icon:"st.Lighting.light14", backgroundColor:"#ffffff", nextState:"turningOn"
            state "turningOn", label:'${name}', icon:"st.Lighting.light14", backgroundColor:"#79b821"
            state "turningOff", label:'${name}', icon:"st.Lighting.light14", backgroundColor:"#ffffff"
        }
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false) {
            state "level", action:"switch level.setLevel"
        }
        controlTile("rgbSelector", "device.color", "color", height: 3, width: 3, inactiveLabel: false) {
            state "color", action:"setAdjustedColor"
        }
  */

 		standardTile("push1", "device.info", width: 6, height: 2,decoration: "flat", canChangeBackground: true) {
			state "default", label: "white colour temperature:"
        }
        valueTile("colorName", "device.colorName", height: 2, width: 4, inactiveLabel: false, decoration: "flat") {
            state "colorName", label: '${currentValue}'
        }
		controlTile("colorTempSliderControl", "device.colorTemperature", "slider", height: 1, width: 6, inactiveLabel: false, range:"(2700..9000)") {
			state "colorTemp", action:"color temperature.setColorTemperature"
		}
        valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", height: 2, width: 2) {
			state "colorTemp", label: '${currentValue}K'
		}
       	standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }


        
        main(["switch"])
        details(["switch", "levelSliderControl", "rgbSelector", "push1","colorTempSliderControl", "colorTemp", "colorName", "refresh"])
    }
}

private debug(data){
    //if(parent.appSettings.debug == "true"){
        log.debug(data)
    //}
}

private getAccessToken() {
    return "PUT YOUR ACCESS TOKEN IN HERE!!";
}

private sendCommand(path, method="GET", body=null) {
    def accessToken = getAccessToken()
    def pollParams = [
        uri: "https://api.lifx.com",
       // path: "/v1beta1/"+path+".json",
		path: "/v1/"+path+".json",
		headers: ["Content-Type": "application/x-www-form-urlencoded", "Authorization": "Bearer ${accessToken}"],
        body: body
    ]
    debug(method+" Http Params ("+pollParams+")")
    
    try{
        if(method=="GET"){
            httpGet(pollParams) { resp ->            
                parseResponse(resp)
            }
        }else if(method=="PUT") {
            httpPut(pollParams) { resp ->            
                parseResponse(resp)
            }
        }
    } catch(Exception e){
        debug("___exception: " + e)
    }
}

private parseResponse(resp) {
    debug("Response: "+resp.data)
    // debug("Results: "+resp.data.results[0])
    
    
    if (resp.status == 404) {
		sendEvent(name: "switch", value: "unreachable")
		return []
	} /*else if (resp.status != 200) {
		log.error("Unexpected result in poll(): [${resp.status}] ${resp.data}")
		return []
	} */
    
    else if (resp.data.results[0] != null){
    log.debug("Results: "+resp.data.results[0])
    }
	else {

	def data = resp.data[0]
	log.debug("Data: ${data}")

	sendEvent(name: "label", value: data.label)
    log.debug("Label: ${data.label}")
	sendEvent(name: "level", value: Math.round((data.brightness ?: 1) * 100))
    log.debug("Label: ${Math.round((data.brightness ?: 1) * 100)}")
	sendEvent(name: "switch.setLevel", value: Math.round((data.brightness ?: 1) * 100))
    log.debug("Label: ${Math.round((data.brightness ?: 1) * 100)}")
	sendEvent(name: "switch", value: data.connected ? data.power : "unreachable")
	sendEvent(name: "color", value: colorUtil.hslToHex((data.color.hue / 3.6) as int, (data.color.saturation * 100) as int))
	sendEvent(name: "hue", value: data.color.hue / 3.6)
	sendEvent(name: "saturation", value: data.color.saturation * 100)
	sendEvent(name: "colorTemperature", value: data.color.kelvin)
	sendEvent(name: "group", value: "${data.group.name}")

	return []
	}
}

//parse events into attributes
def parse(value) {
    debug("Parsing '${value}' for ${device.deviceNetworkId}")
}

private sendAdjustedColor(data, powerOn) {
   // def hue = data.hue*3.6
   // def saturation = data.saturation/100
   // def brightness = data.level/100
    
   	// sendCommand("lights/"+device.deviceNetworkId+"/color", "PUT", 'color=hue%3A'+hue+'%20saturation%3A'+saturation+'%20brightness%3A'+brightness+'&duration=1&power_on='+powerOn)
	// sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", 'color=hue:'+hue+'%20saturation:'+saturation+'%20brightness:'+brightness+'&duration=1&power='+powerOn)
	// sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", [color: "brightness:${brightness}+saturation:${saturation}+hue:${hue}", "power": "on"])
	
    sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", [color: "saturation:${data.saturation / 100}+hue:${data.hue * 3.6}", "power": "on"])
    
    sendEvent(name: 'hue', value: data.hue)
    sendEvent(name: 'saturation', value: data.saturation)
    sendEvent(name: 'level', value: device.currentValue("level"))
    sendEvent(name: 'switch', value: "on")
	
}

def setAdjustedColor(value) {
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = device.currentValue("level")
    
    sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", [color: "saturation:${data.saturation / 100}+hue:${data.hue * 3.6}", "power": "on"])
    
    //sendAdjustedColor(data, 'true')
    // sendEvent(name: "color", value: colorUtil.hslToHex((data.hue) as int, (data.saturation) as int))
    sendEvent(name: 'hue', value: value.hue)
    sendEvent(name: 'saturation', value: value.saturation)
    
    refresh()

}

def setLevel(double value) {
    def data = [:]
    data.hue = device.currentValue("hue")
    data.saturation = device.currentValue("saturation")
    data.level = value
    
    if (data.level < 1 && data.level > 0) {
		data.level = 1 // clamp to 1%
	}
	if (data.level == 0) {
		sendEvent(name: "level", value: 0) // Otherwise the level value tile does not update
		return off() // if the brightness is set to 0, just turn it off
	}
    
    def brightnes = data.level / 100

	sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", ["brightness": brightnes, "power": "on"])

   // sendAdjustedColor(data, 'true')
    sendEvent(name: 'level', value: value)
    sendEvent(name: 'switch', value: "on")
}


def setColor(value) {
    log.debug "setColor: ${value}"
    def data = [:]
    data.hue = value.hue
    data.saturation = value.saturation
    data.level = (value.level)?value.level:device.currentValue("level")
   // data.level = device.currentValue("level")
    
    sendAdjustedColor(data, 'true')
    sendEvent(name: 'hue', value: value.hue)
    sendEvent(name: 'saturation', value: value.saturation)
    sendEvent(name: 'switch', value: "on")
    sendEvent(name: 'level', value: value)
}


def setColorTemperature(value) {
	log.debug "Executing 'setColorTemperature' to ${value}"
	//parent.logErrors() {
		
        sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", [color: "kelvin:${value}", power: "on"])
        //def resp = parent.apiPUT("/lights/${selector()}/state", [color: "kelvin:${kelvin}", power: "on"])
		//if (resp.status < 300) {
		def genericName = getGenericName(value)
    	log.debug "generic name is : $genericName"
            
            sendEvent(name: "colorTemperature", value: value)
			sendEvent(name: "color", value: "#ffffff")
			sendEvent(name: "saturation", value: 0)
            sendEvent(name: "colorName", value: genericName)
		//} else {
		//	log.error("Bad setLevel result: [${resp.status}] ${resp.data}")
		//}

	//}
}

private getGenericName(value){
    def genericName = "Warm White"
    if(value < 2750){
        genericName = "Extra Warm White"
    } else if(value < 3300){
        genericName = "Warm White" 
    } else if(value < 4150){
        genericName = "Moonlight"
    } else if(value < 5000){
        genericName = "Daylight"
    } else if(value < 6500){
        genericName = "Cool Light"
    } else if(value < 8000){
        genericName = "Extra Cool Light"
    } else if(value <= 9000){
        genericName = "Super Cool Light"
    }

    genericName
}

def on() {
    sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", "power=on&duration=1")
   // sendCommand("lights/"+device.deviceNetworkId+"/power", "PUT", "state=on&duration=1")
    sendEvent(name: "switch", value: "on")
}

def off() {
    sendCommand("lights/group_id:"+device.deviceNetworkId+"/state", "PUT", "power=off&duration=1")
  //  sendCommand("lights/"+device.deviceNetworkId+"/power", "PUT", "state=off&duration=1")
    sendEvent(name: "switch", value: "off")
}

def refresh() {
    sendCommand("lights/group_id:"+device.deviceNetworkId)
}

def poll() {
	refresh()
}
