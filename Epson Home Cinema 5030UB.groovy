/**
 *  Copyright 2015 SmartThings
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
	definition (name: "Epson Home Cinema 5030UB", namespace: "AdamV", author: "AdamV") {
		capability "Actuator"
		capability "Button"
		capability "Configuration"
		capability "Sensor"
        
        command "push1"
        command "push2"
        command "push3"
        command "push4"
        command "push5"
       	command "push6"
        command "push7"
        command "push8"
        command "push9"
        command "push10"
        command "push11"
       	command "push12"
        command "push13"
        command "push14"
       	command "push15"
		command "push16"
    	command "push17"
        command "hold1"
        command "hold2"
        command "hold3"
        command "hold4"
	}
/*    preferences {
    section("choose icon") {
        input image(title: "push1",
             required: false)
    }
} */

	simulator {
		status "button 1 pushed":  "command: 2001, payload: 01"
		status "button 1 held":  "command: 2001, payload: 15"
		status "button 2 pushed":  "command: 2001, payload: 29"
		status "button 2 held":  "command: 2001, payload: 3D"
		status "button 3 pushed":  "command: 2001, payload: 51"
		status "button 3 held":  "command: 2001, payload: 65"
		status "button 4 pushed":  "command: 2001, payload: 79"
		status "button 4 held":  "command: 2001, payload: 8D"
		status "wakeup":  "command: 8407, payload: "
	}
	tiles {
		standardTile("button", "device.button", canChangeBackground: true, width: 0, height: 0, decoration: "flat") {
			state "default", label: "CONTROL", icon: "st.Entertainment.entertainment9", backgroundColor: "#ffffff"
		}
 		standardTile("push1", "device.button", width: 1, height: 1 ,decoration: "flat", canChangeBackground: true) {
			state "default", label: "Power On", icon: "http://94.23.40.33/smartthings/assets/power.png",  action: "push1"
		} 
 		standardTile("push2", "device.button", canChangeBackground: true, width: 1, height: 1, decoration: "flat") {
			state "default", label: "Up", icon: "http://94.23.40.33/smartthings/assets/up.png", action: "push3"
		} 
 		standardTile("push3", "device.button", canChangeBackground: true, width: 1, height: 1, decoration: "flat") {
			state "default", label: "Standby", icon: "http://94.23.40.33/smartthings/assets/power.png", action: "push7"
		} 
 		standardTile("push4", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Left", icon: "http://94.23.40.33/smartthings/assets/left.png", action: "push5"
		} 
 		standardTile("dummy1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "OK", icon: "http://94.23.40.33/smartthings/assets/ok.png", action: "push2"
		}         
 		standardTile("hold1", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Right", icon: "http://94.23.40.33/smartthings/assets/right.png", action: "push6"
		}          
 		standardTile("hold2", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Mute", icon: "http://94.23.40.33/smartthings/assets/mute.png", action: "push8"
		}          
 		standardTile("dummy2", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Down", icon: "http://94.23.40.33/smartthings/assets/down.png", action: "push4"
		}         
 		standardTile("hold3", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Back", icon: "http://94.23.40.33/smartthings/assets/back.png", action: "push9"
		}          
 		standardTile("hold4", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "VolDown", icon: "http://94.23.40.33/smartthings/assets/voldown.png", action: "push11"
		}          
        standardTile("push5", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "menu", icon: "http://94.23.40.33/smartthings/assets/menu.png", action: "push12"
		} 
        standardTile("push6", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "VolUp", icon: "http://94.23.40.33/smartthings/assets/volup.png", action: "push13"
		}
        standardTile("push7", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Toggle 2D/3D", icon: "http://94.23.40.33/smartthings/assets/3D.png", action: "push14"
		} 
        standardTile("push8", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "Colour Mode", icon: "http://94.23.40.33/smartthings/assets/colour.png", action: "push15"
		} 
        standardTile("push9", "device.button", width: 1, height: 1, decoration: "flat") {
			state "default", label: "PinP", icon: "http://94.23.40.33/smartthings/assets/picturein.png", action: "push16"
		} 
		main (["button","push1","push2", "push3", "push4"])
		details(["push1","push2","button","push3","push4","dummy1","hold1","hold2","dummy2","hold3","hold4","push5", "push6","push7","push8","push9",])
	}
}

def parse(String description) {
	
}

def push1() {
	push(1)
}

def push2() {
	push(2)
}

def push3() {
	push(3)
}

def push4() {
	push(4)
}

def push5() {
	push(5)
}

def push6() {
	push(6)
}

def push7() {
	push(7)
}

def push8() {
	push(8)
}

def push9() {
	push(9)
}

def push10() {
	push(10)
}

def push11() {
	push(11)
}

def push12() {
	push(12)
}

def push13() {
	push(13)
}

def push14() {
	push(14)
}

def push15() {
	push(15)
}

def push16() {
	push(16)
}

def push17() {
	push(17)
}

def hold1() {
	hold(1)
}

def hold2() {
	hold(2)
}

def hold3() {
	hold(3)
}

def hold4() {
	hold(4)
}

private push(button) {
	log.debug "$device.displayName button $button was pushed"
	sendEvent(name: "button", value: "pushed", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was pushed", isStateChange: true)
}

private hold(button) {
	log.debug "$device.displayName button $button was held"
	sendEvent(name: "button", value: "held", data: [buttonNumber: button], descriptionText: "$device.displayName button $button was held", isStateChange: true)
}
