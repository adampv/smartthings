/**
 * 
 *
 *  
 *
 */

 preferences {
    input("username", "text", title: "Username", description: "Your Foobot username (usually an email address)")
    //input("password", "password", title: "Password", description: "Your Foobot password")
    input("uuid", "text", title: "UUID", description: "The UUID of the exact Foobot that you would like information for")
    //input("APIKey", "text", title: "API Key", description: "The API Key that Foobot gave you")
}
 
metadata {
	definition (name: "Foobot New API", namespace: "AdamV", author: "AdamV") {
		capability "Polling"
        capability "Refresh"
        capability "Sensor"
        capability "Thermostat"
		capability "relativeHumidityMeasurement"
        capability "temperatureMeasurement"
        capability "carbonDioxideMeasurement"
     
     	attribute "pollution", "number"
        attribute "particle", "number"
        attribute "voc", "number"
        attribute "GPIState", "String"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){   

        
        multiAttributeTile(name:"pollution", type:"thermostat", width:6, height:4) {
            tileAttribute("device.pollution", key: "PRIMARY_CONTROL") {
    			attributeState("default", label:'${currentValue}% GPI', unit:"%", icon:"st.Weather.weather13")
  		}
       // valueTile("pollution", "device.pollution", inactiveLabel: false, decoration: "flat") {
       //     state "default", label:'${currentValue}% GPI', unit:"%"
       		tileAttribute("thermostatOperatingState", key: "OPERATING_STATE") {
    			attributeState("great", backgroundColor:"#bc2323")
  				attributeState("good", backgroundColor:"#bc2323")
            	attributeState("fair", backgroundColor:"#ffa81e")
                attributeState("poor", backgroundColor:"#44b621")
       }
       }
        valueTile("co2", "device.co2", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", label:'${currentValue} CO2 ppm', unit:"ppm"
        }
        valueTile("voc", "device.voc", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", label:'${currentValue} VOC ppb', unit:"ppb"
        }
        valueTile("particle", "device.particle", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", label:'${currentValue} µg/m³', unit:"µg/m³ PM2.5"
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", label:'${currentValue}% humidty', unit:"%"
        }
        valueTile("temperature", "device.temperature", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", label:'${currentValue}°', unit:"°"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, width: 2, height: 2, decoration: "flat") {
            state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        main "pollution"
        details(["pollution","co2","voc","particle","humidity", "temperature", "refresh"])
	}
}

private getAPIKey() {
    return "ENTER YOUR API KEY HERE (KEEP THE QUOTATION MARKS)";
	
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
}

private test(){
return "#ffa81e"
}


def refresh() { 
	poll()
}

// handle commands
def poll() {
    
    def start = new Date(Calendar.instance.time.time-1800000).format("yyyy-MM-dd'T'HH:MM':00'");
    def stop = new Date(Calendar.instance.time.time+1800000).format("yyyy-MM-dd'T'HH:MM':00'");
    
    def accessToken = getAPIKey()
    
	//def params = "https://api.foobot.io/v2/owner/${settings.username}/device/?api_key=${accessToken}"
    def params = "https://api.foobot.io/v2/device/${settings.uuid}/datapoint/0/last/0/?api_key=${accessToken}"
    //[
      //  uri:  'https://api.foobot.io',
       // path: "/v2/owner/me@adamvine.co.uk/device/?api_key=eyJhbGciOiJIUzI1NiJ9.eyJncmFudGVlIjoibWVAYWRhbXZpbmUuY28udWsiLCJpYXQiOjE0NzgxMDY1MDksInZhbGlkaXR5IjotMSwianRpIjoiN2NjNGFhOWYtNjcxMS00YjM3LTkxZmMtODQ5YWU4MDY4YjM4IiwicGVybWlzc2lvbnMiOlsidXNlcjpyZWFkIiwiZGV2aWNlOnJlYWQiXSwicXVvdGEiOjIwMCwicmF0ZUxpbWl0Ijo1fQ.AFDvmgWonNSiNijN5PjsiXyVDdaRqZ8rl2MbfiMFoYw",
       // headers: [Authorization : getApiAuth()] ,
       // contentType: 'application/json'
    //]
    try {
        httpGet(params) {resp ->
           resp.headers.each {
           log.debug "${it.name} : ${it.value}"
        }
            // get an array of all headers with the specified key
        	def theHeaders = resp.getHeaders("Content-Length")

        // get the contentType of the response
        	log.debug "response contentType: ${resp.contentType}"

        // get the status code of the response
       		log.debug "response status code: ${resp.status}"

        // get the data from the response body
        	log.debug "response data: ${resp.data}"

            log.debug "pm: ${resp.data.datapoints[-1][1]}"
            sendEvent(name: "particle", value: sprintf("%.2f",resp.data.datapoints[-1][1]), unit: "µg/m³ PM2.5")
            log.debug "tmp: ${resp.data.datapoints[-1][2]}"
            
            BigDecimal tmp = resp.data.datapoints[-1][2]
           	def tmpround = String.format("%5.2f",tmp)
            //def tmpround = tmp.round(2)
            log.debug ("tmpround: $tmpround")
            
            sendEvent(name: "temperature", value: tmpround, unit: "°C")
            log.debug "hum: ${resp.data.datapoints[-1][3]}"
            sendEvent(name: "humidity", value: resp.data.datapoints[-1][3] as Integer, unit: "%")
            log.debug "co2: ${resp.data.datapoints[-1][4]}"
            sendEvent(name: "co2", value: resp.data.datapoints[-1][4] as Integer, unit: "ppm")
            log.debug "voc: ${resp.data.datapoints[-1][5]}"
            sendEvent(name: "voc", value: resp.data.datapoints[-1][5] as Integer, unit: "ppb")
            log.debug "allpollu: ${resp.data.datapoints[-1][6]}"
            def allpollu = resp.data.datapoints[-1][6]
            sendEvent(name: "pollution", value: resp.data.datapoints[-1][6] as Integer, unit: "%")
            if (allpollu < 25){
            	sendEvent(name: "GPIState", value: "great", isStateChange: true)
            }
            else if (allpollu < 50){
            	sendEvent(name: "GPIState", value: "good", isStateChange: true)
            }
            else if (allpollu < 75){
            	sendEvent(name: "GPIState", value: "fair", isStateChange: true)
            }
            else if (allpollu > 75){
            	sendEvent(name: "GPIState", value: "poor", isStateChange: true)
            }
            
          //  def tmp = resp.data.datapoints[-1][2]
          //  if(getTemperatureScale() == "C") {
            //	tmp = Integer.tmp
                
         //   }
          //  else {
            //	tmp = celsiusToFahrenheit(tmp) as Integer
              //  sendEvent(name: "temperature", value: tmp, unit: "°F")
            //}
        }
    } catch (e) {
        log.error "error: $e"
    }
    //sendEvent(name: "GPIState", value: "poor", isStateChange: true)
    def map = [:]
    map.value = "great"
    map.name = "thermostatOperatingState"
	map
    
    log.debug "Http Get params 1"
}

def getApiAuth() {

	/*	rest(
		method: 'POST',
		endpoint: "https://api.foobot.io",
		path: "/v2/user/${settings.username}/login/",
	//	query: [Content-Type: "application/json, Accept: "application/json;charset=UTF-8", X-API-KEY-TOKEN:"${settings.APIKey}", password:"${settings.APIKey}" ],
		synchronous: true
		)
        
    /*    
        def params = [
        uri:  'https://api.foobot.io',
        path: "/v2/user/${settings.username}/login/",
        headers: ["Content-Type": "application/json", "Accept": "application/json;charset=UTF-8", "X-API-KEY-TOKEN":"${settings.APIKey}"],
		body: [format: 'json', body: '{"password":"${settings.APIKey}"}'],
    	]
 
        try {
        httpPostJson(params) {resp ->
            log.debug "resp data: ${resp.data}"
        }
        
       	} catch (e) {
        log.error "error: $e"
    	}
      
      return */
     }

    
