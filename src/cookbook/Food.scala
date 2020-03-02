package cookbook

/* Food describes all ingredients. They are either raw or cooked.
 * Food has a name, a quantity, a unit, and one or more allergenes.
 * Cooked ingredients might be created by cooking. Their allergenes are inherited from ingredients in the recipe.
 */



abstract class Food (val name: String, var quantity: Measure) {
  
  def printName = name
  
  def printAllergen : String
  
  def addAllergen(what: String)
  
  def deleteAllergen()
  
  /* Default values for every food: they can be edited with 'setDensity' 
   * and 'setGramsPerPiece', or from the GUI.
   */
  private var gramsPerPiece : Option[Double] = None
  private var density = 1.0
 
  
  def setDensity(value: Double) = {
    if (value > 0.0) density = value
  }
  
  def returnDensity = density
  
  def setGramsPerPiece(value: Double) = {
    if (value <= 0.0) gramsPerPiece = None
    else gramsPerPiece = Some(value)
  }
  
  
  def returnGramsPerPiece = gramsPerPiece
  

  def hasSameUnit(otherQuantity: Measure) = quantity.unit == otherQuantity.unit
  
  
  def addSome(thisMore: Measure) : Measure = { 
      if (this.hasSameUnit(thisMore)) {
        quantity = this.quantity.add(thisMore.number)
        quantity
      } else {
        addSome(thisMore.convert(this.quantity.unit, returnDensity, returnGramsPerPiece))
      }
    }

  
  def subtractSome(thisMore: Measure) : Measure = { 
      if (this.hasSameUnit(thisMore)) {
        quantity = this.quantity.subtract(thisMore.number)
        quantity
      } else {
        subtractSome(thisMore.convert(this.quantity.unit, returnDensity, returnGramsPerPiece))
      }
    }
  
  
  def isEnough(otherQuantity: Measure) : Boolean = { 
    if (this.hasSameUnit(otherQuantity)) {
      if (quantity.number >= otherQuantity.number) true else false
    } else {
      isEnough(otherQuantity.convert(this.quantity.unit, returnDensity, returnGramsPerPiece))
    }
    }
  
}