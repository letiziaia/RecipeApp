package cookbook

import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import scala.math._

class Recipe (val name: String){
  
  private var ingredients = Map[Food, Measure]()
  private var description = ""
  
  
  def printIngredients = {
    var string = ""
    for (ing <- ingredients) {
      string+=(ing._1.name + ": " + ing._2.number + " " + ing._2.unit + "\n")
      string
    }
    string
  }
  
  def printDescription = description
  
  def addDescription(string: String) = {
    description = string
  }
  
  def saveIngredient(ingredient: Food, quantity: Measure) = {
    val toAdd = (ingredient, quantity)
    ingredients += toAdd
  }
  

  def deleteAllIngredients() {
    ingredients = Map[Food, Measure]()
  }
  
  def getIngredients : Map[Food, Measure]= ingredients

  
  /* For every ingredient of the recipe, checks 
   * if the storage contains more than the required quantity.
   */
  def enoughInStorage : Boolean = {
    getIngredients.forall(r => r._1.isEnough(r._2))
  }
  
  def moreThan0 : Boolean = {
    getIngredients.forall(r => r._1.quantity.number > 0.0)
  }
  
  /* Collects all missing ingredients. */
  def missingIngredients: Buffer[Food] = {
    getIngredients.filter(r => r._1.quantity.number <= 0.0).keys.toBuffer
  }
  
  /* Cooks the recipe by subtracting from each ingredient in storage 
   * the quantity required by the recipe, multiplied by the factor 
   * computer by partialCook (--> see Cookbook). Cooking creates 
   * a Cooked food, whose quantity is the sum in grams of the 
   * quantity of each ingredient used.
   */
  def cook(factor: Double) = {
    ingredients.foreach(p => p._1.subtractSome(new Measure(factor*p._2.number, p._2.unit)))
    val quantity = ingredients
                    .map(f => f._2.convert("grams", f._1.returnDensity, f._1.returnGramsPerPiece))
                    .map(f => f.number*factor).sum
    val unit = "grams" 
    val howMuch = new Measure(quantity, unit)
    new Cooked(this.name, howMuch) 
  }
  
  
  def printRecipe = {
    var s = "Recipe for: " + this.name + "\n" + "You need: " 
    for (ing <- ingredients) {
      s += (ing._1 + ": " + ing._2.number + " " + ing._2.unit + "\n")
    }
    s += this.description
  }

  def printAllergens = {
    var allerg = Set[String]()
    for (ing <- ingredients) {
      val set = ing._1.printAllergen.trim().split(" ").toSet
      allerg = allerg ++ set
    }
    allerg.mkString(" ").trim()
  }
  
  def allergenList : Buffer[String] = {
    printAllergens.split(" ").toBuffer
  }
  
  /* If the recipe contains one ingredient whose name is exactly foodName, 
   * it checks if the quantity of that ingredient required by the recipe is
   * exactly the same as the quantity given as input, after performing the 
   * conversion if needed and if possible.
   */
  def hasIngredientAndQuantity(foodName: String, quantity: Measure) : Boolean = {
    val possibleIng = this.getIngredients.find(p => p._1.name == foodName)
    possibleIng match {
      case None => false
      case Some(f) => {
        val measToComp = f._2.convert(quantity.unit, possibleIng.get._1.returnDensity, possibleIng.get._1.returnGramsPerPiece)
        return (abs(measToComp.number - quantity.number)<0.0001) && (measToComp.unit == quantity.unit)
      }
    }

  }
}