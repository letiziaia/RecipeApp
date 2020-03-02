package cookbook

import scala.collection.mutable.Buffer

class Cooked(name: String, quantity: Measure) extends Food(name, quantity) {
  
  private var allergen = Set[String]()
  
  override def addAllergen(what: String) = {
    for(el <- what.trim().split(' ')){
    allergen += el.trim()
    }
  }
  
  def printAllergen =  allergen.mkString(" ").trim()
  
  def deleteAllergen() = Set[String]()
    
}