println "@prefix : <http://aber-owl.net/coral/coro.owl#> ."
println "@prefix reef: <http://aber-owl.net/coral/coro.owl#Reef:> ."
println "@prefix reeftype: <http://aber-owl.net/coral/coro.owl#Reeftype:> ."
println "@prefix location: <http://aber-owl.net/coral/coro.owl#Location:> ."
println "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ."
println "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."
println "@prefix owl: <http://www.w3.org/2002/07/owl#> ."
println "@prefix disease: <http://aber-owl.net/coral/coro.owl#Disease:> ."
println "@prefix diseasetype: <http://aber-owl.net/coral/coro.owl#DiseaseType:> ."
println "@prefix diseaselocation: <http://aber-owl.net/coral/coro.owl#DiseaseLocation:> ."

def reeftypes = [:]
def counter = 0
def reeflabel2id = [:]
new File("ReefLocations.csv").eachLine { l -> 
  def line = l.split(',(?=([^\"]*\"[^\"]*\")*[^\"]*$)')
  if (! line[0].startsWith("ID")) {
    def id = line[0]
    println "location:$id a :GeoLocation ."
    
    def region = line[1]?.replaceAll("\"","")
    def subregion = line[2]?.replaceAll("\"","")
    def country = line[3]?.replaceAll("\"","")
    def location = line[4]?.replaceAll("\"","")
    def lat = line[5]
    def lon = line[6]
    def depth = line[10]
    def islandname = line[11]?.replaceAll("\"","")

    if (region) {
      println "location:$id :has_region \"$region\" ."
    }
    if (subregion) {
      println "location:$id :has_subregion \"$subregion\" ."
    }
    if (country) {
      println "location:$id :has_country \"$country\" ."
    }
    if (location) {
      println "location:$id :has_location \"$location\" ."
    }
    if (lat) {
      println "location:$id :latitude $lat ."
    }
    if (lon) {
      println "location:$id :longitude $lon ."
    }
    if (depth) {
      println "location:$id :depth $depth ."
    }
    if (islandname) {
      println "location:$id :island \"$islandname\" ."
    }

    def rsystem = line[7]?.replaceAll("\"","")
    def rtype = line[8]?.replaceAll("\"","")?.split("/")
    def rname = line[9]?.replaceAll("\"","")
 
    println "reef:$id a :Reef ."
    println "reef:$id :located_in location:$id ."

    println "reef:$id rdfs:label \"$rname\" ."
    reeflabel2id[rname] = id
    if (rtype) {
      rtype.each { r ->
	if (r && r.size()>0) {
	  r = r.toLowerCase()
	  if (! (r in reeftypes.keySet())) {
	    println "reeftype:$counter rdfs:subClassOf :Reef ."
	    println "reeftype:$counter rdfs:label \"$r\" ."
	    reeftypes[r] = counter
	    counter += 1
	  }
	  def rnum = reeftypes[r]
	  println "reef:$id a reeftype:$rnum ."
	}
      }
    }

    def is_protected = line[12]
    def is_touristic = line[13]
    if (is_protected == "1") {
      println "reeftype:$id a :Protected_reef ."
    }
    if (is_touristic == "1") {
      println "reeftype:$id a :Touristic_reef ."
    }
  }
}


def location2id = [:]
def loccounter = 0

def disease2id = [:] // enumerates disease types
def discounter = 0
new File("CoralDiseases.csv").eachLine { l -> 
  def line = l.split(',(?=([^\"]*\"[^\"]*\")*[^\"]*$)')
  if (! line[0].startsWith("ID") && (line.size() == 19)) {
    def id = line[0]
    def diseasetype = line[1]?.toLowerCase()?.replaceAll("\"","")
    if (!(diseasetype in disease2id.keySet())) {
      disease2id[diseasetype] = discounter
      println "diseasetype:$discounter rdfs:subClassOf :Disease ."
      println "diseasetype:$discounter rdfs:label \"$diseasetype\" ."
      discounter += 1
    }
    def diseaseid = disease2id[diseasetype]
    println "disease:$id a [ rdf:type owl:Restriction ; owl:onProperty :of ; owl:someValuesFrom diseasetype:$diseaseid ] ."
    println "disease:$id a :DiseaseObservation ."

    def year = line[2]?.replaceAll("\"","")
    def lat = line[3]?.replaceAll("\"","")
    def lon = line[4]?.replaceAll("\"","")
    def region = line[5]?.replaceAll("\"","")
    def subregion = line[6]?.replaceAll("\"","")
    def country = line[7]?.replaceAll("\"","")
    def location = line[8]?.replaceAll("\"","")
    def town = line[9]?.replaceAll("\"","")
    
    if (lat && lon) {
      if (! ( (lat+" "+lon) in location2id)) {
	println "diseaselocation:$loccounter a :GeoLocation ."
	if (lat) {
	  println "diseaselocation:$loccounter :latitude $lat ."
	}
	if (long) {
	  println "diseaselocation:$loccounter :longitude $lon ."
	}
	if (region) {
	  println "diseaselocation:$loccounter :region \"$region\" ."
	}
	if (subregion) {
	  println "diseaselocation:$loccounter :subregion \"$subregion\" ."
	}
	if (country) {
	  println "diseaselocation:$loccounter :country \"$country\" ."
	}
	if (location) {
	  println "diseaselocation:$loccounter :location \"$location\" ."
	}
	if (town) {
	  println "diseaselocation:$loccounter :near \"$town\" ."
	}
	println "disease:$id :located_in diseaselocation:$loccounter ."
	location2id[lat+" "+lon] = loccounter
	loccounter += 1
      } else {
	def lc = location2id[lat+" "+lon]
	println "disease:$id :located_in diseaselocation:$lc ."
      }
      if (year) {
	println "disease:$id :year $year ."
      }
    }
  }
}