/**
 *  Trimmed Virtual Version of Fibaro RGBW Controller - All code originated per metadata below.
 *
 *  Device Type Definition File
 *
 *  Device Type:		Virtual Fibaro RGBW Controller
 *  File Name:			fibaro-rgbw-controller.groovy
 *	Initial Release:	2015-01-04
 *	Author:				Todd Wackford
 *  Email:				todd@wackford.net
 *
 *  Copyright 2015 SmartThings
 *
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
 
 /*
 Todo's
    1. Incorporate javadoc information and formatting
 */
 
 metadata {

	definition (name: "Virtual Light RGBW Controller", namespace: "smartthings", author: "Todd Wackford") {
		capability "Switch Level"
		capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Refresh"
		capability "Sensor"
		capability "Configuration" 
		capability "Color Control"
        capability "Power Meter"
       
       	command "levelDown"
		command "levelUp"
        command "getDeviceData"
        command "softwhite"
        command "daylight"
        command "warmwhite"
        command "red"
        command "green"
        command "blue"
        command "cyan"
        command "magenta"
        command "orange"
        command "purple"
        command "yellow"
        command "white"
        command "fireplace"
        command "storm"
        command "deepfade"
        command "litefade"
        command "police"
        command "setAdjustedColor"
        command "setWhiteLevel"
        command "test"
        
        attribute "whiteLevel", "string"
      

	}
    
    simulator {
    	status "on":  "command: 2003, payload: FF"
    	status "off": "command: 2003, payload: 00"
    	status "09%": "command: 2003, payload: 09"
    	status "10%": "command: 2003, payload: 0A"
    	status "33%": "command: 2003, payload: 21"
    	status "66%": "command: 2003, payload: 42"
    	status "99%": "command: 2003, payload: 63"

     	// reply messages
     	reply "2001FF,delay 5000,2602": "command: 2603, payload: FF"
     	reply "200100,delay 5000,2602": "command: 2603, payload: 00"
     	reply "200119,delay 5000,2602": "command: 2603, payload: 19"
     	reply "200132,delay 5000,2602": "command: 2603, payload: 32"
     	reply "20014B,delay 5000,2602": "command: 2603, payload: 4B"
     	reply "200163,delay 5000,2602": "command: 2603, payload: 63"
	}

	tiles (scale: 2){
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#fffA62", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#fffA62", nextState:"turningOn"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"setAdjustedColor"
			}
		}  
        valueTile("power", "device.power", decoration: "flat") {
            state "power", label:'${currentValue} W'
        }
        standardTile("lUp", "device.switchLevel", inactiveLabel: false,decoration: "flat", canChangeIcon: false) {
                        state "default", action:"levelUp", icon:"st.illuminance.illuminance.bright"
        }
        standardTile("lDown", "device.switchLevel", inactiveLabel: false,decoration: "flat", canChangeIcon: false) {
                        state "default", action:"levelDown", icon:"st.illuminance.illuminance.light"
        }
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        standardTile("refresh", "device.switch", height: 1, inactiveLabel: false, decoration: "flat") {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("softwhite", "device.softwhite", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offsoftwhite", label:"soft white", action:"softwhite", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onsoftwhite", label:"soft white", action:"softwhite", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFF1E0"
        }
        standardTile("daylight", "device.daylight", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offdaylight", label:"daylight", action:"daylight", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "ondaylight", label:"daylight", action:"daylight", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFB"
        }
        standardTile("warmwhite", "device.warmwhite", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offwarmwhite", label:"warm white", action:"warmwhite", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onwarmwhite", label:"warm white", action:"warmwhite", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFF4E5"
        }
        standardTile("red", "device.red", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offred", label:"red", action:"red", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onred", label:"red", action:"red", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("green", "device.green", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offgreen", label:"green", action:"green", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "ongreen", label:"green", action:"green", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FF00"
        }
        standardTile("blue", "device.blue", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.bright", backgroundColor:"#0000FF"
        }
        standardTile("cyan", "device.cyan", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offcyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "oncyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FFFF"
        }
        standardTile("magenta", "device.magenta", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF00FF"
        }
        standardTile("orange", "device.orange", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF6600"
        }
        standardTile("purple", "device.purple", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.bright", backgroundColor:"#BF00FF"
        }
        standardTile("yellow", "device.yellow", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFF00"
        }
        standardTile("white", "device.white", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offwhite", label:"White", action:"white", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onwhite", label:"White", action:"white", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        standardTile("fireplace", "device.fireplace", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offfireplace", label:"Fire Place", action:"fireplace", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onfireplace", label:"Fire Place", action:"fireplace", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        standardTile("storm", "device.storm", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offstorm", label:"storm", action:"storm", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onstorm", label:"storm", action:"storm", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        standardTile("deepfade", "device.deepfade", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offdeepfade", label:"deep fade", action:"deepfade", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "ondeepfade", label:"deep fade", action:"deepfade", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        standardTile("litefade", "device.litefade", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offlitefade", label:"lite fade", action:"litefade", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onlitefade", label:"lite fade", action:"litefade", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        standardTile("police", "device.police", height: 1, inactiveLabel: false, canChangeIcon: false) {
            state "offpolice", label:"police", action:"police", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "onpolice", label:"police", action:"police", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        controlTile("saturationSliderControl", "device.saturation", "slider", height: 1, width: 2, inactiveLabel: false) {
			state "saturation", action:"color control.setSaturation"
		}
		valueTile("saturation", "device.saturation", inactiveLabel: false, decoration: "flat") {
			state "saturation", label: 'Sat ${currentValue}    '
		}
		controlTile("hueSliderControl", "device.hue", "slider", height: 1, width: 2, inactiveLabel: false) {
			state "hue", action:"color control.setHue"
		}
		valueTile("hue", "device.hue", inactiveLabel: false, decoration: "flat") {
			state "hue", label: 'Hue ${currentValue}   '
		}
        
        main(["switch"])
        details(["switch", 
                 "levelSliderControl", 
                 "rgbSelector", 
                 "whiteSliderControl", 
                 "lUp",
                 "lDown",
                 "softwhite",
                 "daylight",
                 "warmwhite",
                 "red", 
                 "green", 
                 "blue",
                 "cyan",
                 "magenta",
                 "orange",
                 "purple",
                 "yellow",
                 "white",
                 "fireplace",
                 "storm",
                 "deepfade",
                 "litefade",
                 "police",
                 //"power",
                 //"configure",
                 "refresh"])
	}
}

def initialize() {   
	if ( !settings.stepsize )
    	state.stepsize = 10
 //   else
//		state.stepsize = settings.stepsize
    
    if (!device.currentValue("level"))
    	setLevel(100)
}

def levelUp(){
	if ( !state.stepsize ) {
    	initialize()
        log.info "initialized on first up"
    } 
    //else {
   // 	state.stepsize = settings.stepsize
  //  }
    
        
    int thisStep = state.stepsize
    int nextLevel = device.currentValue("level") + thisStep
    
    if( nextLevel > 100){
    	nextLevel = 100
    }
    
    log.debug "Setting dimmer level up to: ${nextLevel}"


	//send event since most zwave devices don't publish the level event until polled
    sendEvent(name:"level",value:nextLevel)
    sendEvent(name:"switch.setLevel",value:nextLevel)
}

def levelDown(){
	if ( !state.stepsize ) {
    	initialize()
        log.info "initialized on first up"
    } 
    //else {
    //	state.stepsize = settings.stepsize
    // }
    
    int thisStep = state.stepsize 
    int nextLevel = device.currentValue("level") - thisStep
    
    if (nextLevel < 1){
    	nextLevel = 0
    }
    
    if (nextLevel == 0){
    	off()
    }
    else
    {
    	log.debug "Setting dimmer level down to: ${nextLevel}"
    
    	//send event since most zwave devices don't publish the level event until polled
    	sendEvent(name:"level",value:nextLevel)
        sendEvent(name:"switch.setLevel",value:nextLevel)
    }
}


def setAdjustedColor(value) {
	log.debug "setAdjustedColor: ${value}"
    
    toggleTiles("off") //turn off the hard color tiles

    def level = device.latestValue("level")
    value.level = level

	def c = hexToRgb(value.hex) 
	value.rh = hex(c.r * (level/100))
	value.gh = hex(c.g * (level/100))
	value.bh = hex(c.b * (level/100))
	
    log.debug "setColor: ${value}"
    setColor(value)
    
    sendEvent(name: "adjustedColor", value: value, displayed: false)
}

def setColor(value) {
	log.debug "setColor: ${value}"
    
    if (value.size() < 8)
    	toggleTiles("off")

    if (( value.size() == 2) && (value.hue != null) && (value.saturation != null)) { //assuming we're being called from outside of device (App)
    	def rgb = hslToRGB(value.hue, value.saturation, 0.5)
        value.hex = rgbToHex(rgb)
        value.rh = hex(rgb.r)
        value.gh = hex(rgb.g)
        value.bh = hex(rgb.b)
    }
    
    if ((value.size() == 3) && (value.hue != null) && (value.saturation != null) && (value.level)) { //user passed in a level value too from outside (App)
    	def rgb = hslToRGB(value.hue, value.saturation, 0.5)
        value.hex = rgbToHex(rgb)
        value.rh = hex(rgb.r * value.level/100)
        value.gh = hex(rgb.g * value.level/100)
        value.bh = hex(rgb.b * value.level/100)       
    }
    
    if (( value.size() == 1) && (value.hex)) { //being called from outside of device (App) with only hex
		def rgbInt = hexToRgb(value.hex)
        value.rh = hex(rgbInt.r)
        value.gh = hex(rgbInt.g)
        value.bh = hex(rgbInt.b)
    }
    
    if (( value.size() == 2) && (value.hex) && (value.level)) { //being called from outside of device (App) with only hex and level

        def rgbInt = hexToRgb(value.hex)
        value.rh = hex(rgbInt.r * value.level/100)
        value.gh = hex(rgbInt.g * value.level/100)
        value.bh = hex(rgbInt.b * value.level/100)
    }
    
    if (( value.size() == 1) && (value.colorName)) { //being called from outside of device (App) with only color name
        def colorData = getColorData(value.colorName)
        value.rh = colorData.rh
        value.gh = colorData.gh
        value.bh = colorData.bh
        value.hex = "#${value.rh}${value.gh}${value.bh}"
    }
    
    if (( value.size() == 2) && (value.colorName) && (value.level)) { //being called from outside of device (App) with only color name and level
		def colorData = getColorData(value.colorName)
        value.rh = hex(colorData.r * value.level/100)
        value.gh = hex(colorData.g * value.level/100)
        value.bh = hex(colorData.b * value.level/100)
        value.hex = "#${hex(colorData.r)}${hex(colorData.g)}${hex(colorData.b)}"
    }
    
    if (( value.size() == 3) && (value.red != null) && (value.green != null) && (value.blue != null)) { //being called from outside of device (App) with only color values (0-255)
        value.rh = hex(value.red)
        value.gh = hex(value.green)
        value.bh = hex(value.blue)
        value.hex = "#${value.rh}${value.gh}${value.bh}"
    }

    if (( value.size() == 4) && (value.red != null) && (value.green != null) && (value.blue != null) && (value.level)) { //being called from outside of device (App) with only color values (0-255) and level
        value.rh = hex(value.red * value.level/100)
        value.gh = hex(value.green * value.level/100)
        value.bh = hex(value.blue * value.level/100)
        value.hex = "#${hex(value.red)}${hex(value.green)}${hex(value.blue)}"
    }
    
    sendEvent(name: "hue", value: value.hue, displayed: false)
	sendEvent(name: "saturation", value: value.saturation, displayed: false)
	sendEvent(name: "color", value: value.hex, displayed: false)
	if (value.level) {
		sendEvent(name: "level", value: value.level, isStateChange: true)
	}
	if (value.switch) {
		sendEvent(name: "switch", value: value.switch, isStateChange: true)
	}
	   
    sendRGB(value.rh, value.gh, value.bh)
}

def setLevel(level) {
	log.trace "setLevel($level)"
    
	if (level == 0) { off() }
	else if (device.latestValue("switch") == "off") { on() }
    
    def colorHex = device.latestValue("color")
    def c = hexToRgb(colorHex)
    
    def r = hex(c.r * (level/100))
    def g = hex(c.g * (level/100))
    def b = hex(c.b * (level/100))
    
	sendEvent(name: "level", value: level)
    sendEvent(name: "setLevel", value: level, displayed: false)
	sendRGB(r, g, b)
}


def setWhiteLevel(value) {
	log.debug "setWhiteLevel: ${value}"
    def level = Math.min(value as Integer, 99)    
    level = 255 * level/99 as Integer
    def channel = 0

    sendEvent(name: "whiteLevel", value: value)
    sendWhite(channel, value)       
}

def sendWhite(channel, value) {
	def whiteLevel = hex(value)
    def cmd = [String.format("3305010${channel}${whiteLevel}%02X", 50)]
    cmd
}

def sendRGB(redHex, greenHex, blueHex) {

}


def configure() {
	log.debug "No Configuration Needed for Virtual Device"

}

def parse(String description) {

}


def on() {
	log.debug "on()"
	sendEvent(name: "switch", value: "on")
}

def off() {
	log.debug "off()"
	sendEvent(name: "switch", value: "off")
}


def poll() {
    log.debug "poll()"
    refresh()
}

def refresh() {
	log.debug "refresh()"
}


def colorNameToRgb(color) {

	final colors = [
        [name:"Soft White",	r: 255, g: 241, b: 224	],
        [name:"Daylight", 	r: 255, g: 255, b: 251	],
        [name:"Warm White", r: 255, g: 244, b: 229	],
        
        [name:"Red", 		r: 255, g: 0,	b: 0	],
		[name:"Green", 		r: 0, 	g: 255,	b: 0	],
        [name:"Blue", 		r: 0, 	g: 0,	b: 255	],
        
		[name:"Cyan", 		r: 0, 	g: 255,	b: 255	],
        [name:"Magenta", 	r: 255, g: 0,	b: 33	],       
        [name:"Orange", 	r: 255, g: 102, b: 0	],
        
        [name:"Purple", 	r: 170, g: 0,	b: 255	],
		[name:"Yellow", 	r: 255, g: 255, b: 0	],
        [name:"White", 		r: 255, g: 255, b: 255	]
	]
    
    def colorData = [:]    
    colorData = colors.find { it.name == color }
    
    colorData
}

private hex(value, width=2) {
	def s = new BigInteger(Math.round(value).toString()).toString(16)
	while (s.size() < width) {
		s = "0" + s
	}
	s
}

def hexToRgb(colorHex) {
	def rrInt = Integer.parseInt(colorHex.substring(1,3),16)
    def ggInt = Integer.parseInt(colorHex.substring(3,5),16)
    def bbInt = Integer.parseInt(colorHex.substring(5,7),16)
    
    def colorData = [:]
    colorData = [r: rrInt, g: ggInt, b: bbInt]
    colorData
}

def rgbToHex(rgb) {
    def r = hex(rgb.r)
    def g = hex(rgb.g)
    def b = hex(rgb.b)
    def hexColor = "#${r}${g}${b}"
    
    hexColor
}

def hslToRGB(float var_h, float var_s, float var_l) {
	float h = var_h / 100
    float s = var_s / 100
    float l = var_l
    
    def r = 0
    def g = 0
    def b = 0
    
	if (s == 0) {
    	r = l * 255
        g = l * 255
        b = l * 255
	} else {
    	float var_2 = 0
    	if (l < 0.5) {
        	var_2 = l * (1 + s)
        } else {
        	var_2 = (l + s) - (s * l)
        }
                
        float var_1 = 2 * l - var_2
        
        r = 255 * hueToRgb(var_1, var_2, h + (1 / 3))
        g = 255 * hueToRgb(var_1, var_2, h)
        b = 255 * hueToRgb(var_1, var_2, h - (1 / 3))    
    }
    
    def rgb = [:]
    rgb = [r: r, g: g, b: b]

    rgb 
}

def hueToRgb(v1, v2, vh) {
	if (vh < 0) { vh += 1 }            
	if (vh > 1) { vh -= 1 }
	if ((6 * vh) < 1) { return (v1 + (v2 - v1) * 6 * vh) }
    if ((2 * vh) < 1) { return (v2) }
    if ((3 * vh) < 2) { return (v1 + (v2 - $v1) * ((2 / 3 - vh) * 6)) }    
    return (v1)
}

def rgbToHSL(rgb) {
	def r = rgb.r / 255
    def g = rgb.g / 255
    def b = rgb.b / 255
    def h = 0
    def s = 0
    def l = 0
    
    def var_min = [r,g,b].min()
    def var_max = [r,g,b].max()
    def del_max = var_max - var_min
    
    l = (var_max + var_min) / 2
    
    if (del_max == 0) {
            h = 0
            s = 0
    } else {
    	if (l < 0.5) { s = del_max / (var_max + var_min) } 
        else { s = del_max / (2 - var_max - var_min) }

        def del_r = (((var_max - r) / 6) + (del_max / 2)) / del_max
        def del_g = (((var_max - g) / 6) + (del_max / 2)) / del_max
        def del_b = (((var_max - b) / 6) + (del_max / 2)) / del_max

        if (r == var_max) { h = del_b - del_g } 
        else if (g == var_max) { h = (1 / 3) + del_r - del_b } 
        else if (b == var_max) { h = (2 / 3) + del_g - del_r }
        
		if (h < 0) { h += 1 }
        if (h > 1) { h -= 1 }
	}
    def hsl = [:]    
    hsl = [h: h * 100, s: s * 100, l: l]
    
    hsl
}

def getColorData(colorName) {
	log.debug "getColorData: ${colorName}"
    
    def colorRGB = colorNameToRgb(colorName)
    def colorHex = rgbToHex(colorRGB)
	def colorHSL = rgbToHSL(colorRGB)
        
    def colorData = [:]
    colorData = [h: colorHSL.h, 
    			 s: colorHSL.s, 
                 l: device.latestValue("level"), 
                 r: colorRGB.r, 
                 g: colorRGB.g,
                 b: colorRGB.b,
                 rh: hex(colorRGB.r),
                 gh: hex(colorRGB.g),
                 bh: hex(colorRGB.b),
                 hex: colorHex,
                 alpha: 1]
     
     colorData                 
}

def doColorButton(colorName) {
    log.debug "doColorButton: '${colorName}()'"

    if (device.latestValue("switch") == "off") { on() }

    def level = device.latestValue("level")

    toggleTiles(colorName.toLowerCase().replaceAll("\\s",""))
    
    if 		( colorName == "Fire Place" ) 	{  }
	else if ( colorName == "Storm" ) 		{  }
    else if ( colorName == "Deep Fade" ) 	{  }
    else if ( colorName == "Lite Fade" ) 	{  }
    else if ( colorName == "Police" ) 		{  }
    else {
		def c = getColorData(colorName)
		def newValue = ["hue": c.h, "saturation": c.s, "level": level, "red": c.r, "green": c.g, "blue": c.b, "hex": c.hex, "alpha": c.alpha]  
    	setColor(newValue)            
    	def r = hex(c.r * (level/100))
    	def g = hex(c.g * (level/100))
    	def b = hex(c.b * (level/100))
		sendRGB(r, g, b)
    }
}

def toggleTiles(color) {
	state.colorTiles = []
	if ( !state.colorTiles ) {
    	state.colorTiles = ["softwhite","daylight","warmwhite","red","green","blue","cyan","magenta","orange","purple","yellow","white","fireplace","storm","deepfade","litefade","police"]
    }
    
    def cmds = []
    
    state.colorTiles.each({
    	if ( it == color ) {
        	log.debug "Turning ${it} on"
            device.displayName + " was closed"
            cmds << sendEvent(name: it, value: "on${it}", display: True, descriptionText: "${device.displayName} ${color} is 'ON'", isStateChange: true)
        } else {
        	//log.debug "Turning ${it} off"
        	cmds << sendEvent(name: it, value: "off${it}", displayed: false)
        }
    })
    
    delayBetween(cmds, 2500)
}

// rows of buttons
def softwhite() { sendEvent(name: "presetColor", value: "Soft White") 
				  doColorButton("Soft White") }
                  
def daylight()  { sendEvent(name: "presetColor", value: "Daylight")
				  doColorButton("Daylight") }
                  
def warmwhite() { sendEvent(name: "presetColor", value: "Warm White")
				  doColorButton("Warm White") }
                  

def red() 		{ sendEvent(name: "presetColor", value: "Red")
				  doColorButton("Red") }
                  
def green() 	{ sendEvent(name: "presetColor", value: "Green")
				  doColorButton("Green") }
                  
def blue() 		{ sendEvent(name: "presetColor", value: "Blue")
				  doColorButton("Blue") }
                  

def cyan() 		{ sendEvent(name: "presetColor", value: "Cyan")
				  doColorButton("Cyan") }
                  
def magenta()	{ sendEvent(name: "presetColor", value: "Magenta")
				  doColorButton("Magenta") }
                  
def orange() 	{ sendEvent(name: "presetColor", value: "Orange")
				  doColorButton("Orange") }
                  

def purple()	{ sendEvent(name: "presetColor", value: "Purple")
				  doColorButton("Purple") }
                  
def yellow() 	{ sendEvent(name: "presetColor", value: "Yellow")
				  doColorButton("Yellow") }
                  
def white() 	{ sendEvent(name: "presetColor", value: "White")
				  doColorButton("White") }


def fireplace() { sendEvent(name: "presetColor", value: "Fire Place")
				  doColorButton("Fire Place") }
                  
def storm() 	{ sendEvent(name: "presetColor", value: "Storm")
				  doColorButton("Storm") }
					
def deepfade() 	{ sendEvent(name: "presetColor", value: "Deep Fade")
				  doColorButton("Deep Fade") }


def litefade() 	{ sendEvent(name: "presetColor", value: "Lite Fade")
				  doColorButton("Lite Fade") }
                  
def police() 	{ sendEvent(name: "presetColor", value: "Police")
				  doColorButton("Police") }
