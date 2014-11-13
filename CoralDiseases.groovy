println "@prefix : <http://aber-owl.net/coral/coro.owl#> ."
println "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> ."

def reeftypes = [:]
def counter = 0
new File("CoralDiseases.csv").eachLine { l -> 
  def line = l.split(',(?=([^\"]*\"[^\"]*\")*[^\"]*$)')
  if (! line[0].startsWith

}