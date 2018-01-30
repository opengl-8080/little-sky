import groovy.json.*

def slurper = new JsonSlurper()
def json = new File(args[0]).getText("UTF-8")
def root = slurper.parseText(json)

new File("./cities.txt").withWriter("UTF-8") { writer ->
    root.each { city ->
        writer << "${city.id}\t${city.country}\t${city.name}\t${city.coord.lon}\t${city.coord.lat}\n"
    }
}