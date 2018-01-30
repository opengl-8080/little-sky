import java.net.*

def apiKey = args[0]
def timestamp = "1458000000"

new File("./cities.txt").eachLine('UTF-8') { line ->
    def elements = line.split("\t")
    def id = elements[0]
    def country = elements[1]
    def name = elements[2]
    def lon = elements[3]
    def lat = elements[4]

    println("id=$id, country=$country, name=$name, lon=$lon, lat=$lat")

    def url = "https://maps.googleapis.com/maps/api/timezone/json?location=$lat,$lon&timestamp=$timestamp&key=$apiKey"
    def response = new StringBuilder()
    def con = new URL(url).openConnection()
    con.getInputStream().withReader('UTF-8') { reader ->
        def buff = null
        while ((buff = reader.readLine()) != null) {
            response << buff + "\n"
        }
        println(response)
    }
    System.exit(0)
    Thread.sleep(1000)
}