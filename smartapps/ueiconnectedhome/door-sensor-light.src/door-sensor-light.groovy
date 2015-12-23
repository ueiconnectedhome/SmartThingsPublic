definition(
name: "door_sensor_light",
namespace: "ueiconnectedhome",
author: "Maryam",
description: "Light Control with Sensor",
category: "Convenience",
iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
oauth: [displayName: "web services tutorial ", displayLink: "http://localhost:4567"])
preferences {
  section ("Allow external service to control these things...") {
    input "switches", "capability.color Control", multiple: true, required: true
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
    //resp << [name: it.displayName, value: it.currentValue("switch"), color: it.currentValue("color")]
    resp << [name: it.displayName, value: it.currentValue("switch")]
  }

  //return resp
  
  render contentType: "application/javascript", data: "${params.callback}(${resp.encodeAsJSON()})"
  //render contentType: "application/javascript", data: "${params.callback}(${switches.collect{[id: it.id, name: it.displayName, status: 'hello']}.encodeAsJSON()})"
}

def listContactSensors() {
  def resp = []
  contacts.each {
    resp << [name: it.displayName, value: it.currentValue("contact")]
  }
  //return resp
  render contentType: "application/javascript", data: "${params.callback}(${resp.encodeAsJSON()})"
}

void updateSwitches() {

  // use the built-in request object to get the command parameter
  def command = params.command
  //def value = [switch: "on", hue: 70, saturation: 100, level: 100, hex: "#C6A738" ]
  //def value= [level:100, hex:"#C6A738", saturation:56, hue:23, switch: "on"]
  //log.debug "setColor: ${value}, $this"

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
  
  switches.each 
  { 
     if("on" == "$command") {
      //it.setHue(70)
      //it.setSaturation(100)  
      //it.setLevel(100)
      //it.setColor($value) //red: "#FF0000"
      it.on()  // Turn the bulb on when open (this method does not come directly from the colorControl capability)
  
    } else {
      it.off()  // Turn the bulb off when closed (this method does not come directly from the colorControl capability)
    }
  }
  }
}

def installed() { 

}

def switchOnHandler(evt) {
    log.debug "switch turned on!"
    // call CCE.event 
}

def updated() {}