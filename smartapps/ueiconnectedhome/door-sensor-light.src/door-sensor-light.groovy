definition(
  name: "door_sensor_light",
  namespace: "ueiconnectedhome",
  author: "Maryam",
  description: "Light Control with Sensor",
  category: "Convenience",
  iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
  iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
  oauth: [displayName: "web services tutorial ", displayLink: "http://localhost:4567"]
)

preferences {
  section ("Allow external service to control these things...") {
    input "switches", "capability.switch", multiple: true, required: true
  }
  section("Monitor this door or window") {
    input "contacts", "capability.contactSensor"
  }
}

mappings {
  path("/switches") {
    action: [
      GET: "listSwitches"
    ]
  }

  path("/contacts") {
    action: [
      GET: "listContactSensors"
    ]
  }

  path("/switches/:command") {
    action: [
      GET: "updateSwitches"
    ]
  }
}

// returns a list like
// [[name: "kitchen lamp", value: "off"], [name: "bathroom", value: "on"]]
def listSwitches() {
  def resp = []
  switches.each {
    resp << [name: it.displayName, value: it.currentValue("switch")]
  }
return resp
//render contentType: "application/javascript", data: "${params.callback}(${resp.encodeAsJSON()})"
}

def listContactSensors() {
  def resp = []
  contacts.each {
    resp << [name: it.displayName, value: it.currentValue("contact")]
  }
return resp
//render contentType: "application/javascript", data: "${params.callback}(${resp.encodeAsJSON()})"
}

void updateSwitches() {
  // use the built-in request object to get the command parameter
  def command = params.command
  def value = [switch: "on", hue: hueColor, saturation: saturation, level: level as Integer ?: 100]

  if (command) {
    // check that the switch supports the specified command
    // If not, return an error using httpError, providing a HTTP status code.
    switches.each {
      if (!it.hasCommand(command)) {
        httpError(501, "$command is not a valid command for all switches specified")
      }
    }

// all switches have the comand
// execute the command on all switches
// (note we can do this on the array - the command will be invoked on every element
//switches."$command"()
    if("on" == "$command") {
      switches.setHue(70)
      switches.setSaturation(100) // Set the color to something fancy
      switches.setLevel(100) // Make sure the light brightness is 100%
      switches.on() // Turn the bulb on when open (this method does not come directly from the colorControl capability)
    } else {
      switches.off() // Turn the bulb off when closed (this method does not come directly from the colorControl capability)
    }
  }
}

def doorSensorHandler(evt) {

  if("open" == "${evt.value}") {
  
    switches.on()
    
  } else {
    
    switches.off()
  
  }
}

def installed() { 

 log.debug "Installed with settings: ${settings}"
 subscribe(contacts, "contact", doorSensorHandler )
 
}

def updated() {

subscribe(contacts, "contact", doorSensorHandler )

}