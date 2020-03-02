package cookbook


class Raw(name: String, quantity: Measure) extends Food(name, quantity) {
  
  private var allergen : String = ""
  
  override def addAllergen(what: String) = {
    allergen=what.trim().split(' ').head.trim()
  }
  
  def printAllergen = allergen.trim()
  
  def deleteAllergen() = allergen = ""
  
}