package cookbook

import scala.collection.mutable.Buffer
import scala.util.Try
import java.io._
import scala.math._

class Cookbook {
  
  val storage = new Storage("some storage")
  
  var allRecipes = Buffer[Recipe]()
  
  private var okToCook = Buffer[Recipe]()
  
  def addRecipe(recipe: Recipe) = {
    allRecipes += recipe
  }
  
  def deleteRecipe(name: String) = {
    if (this.containsRecipe(name)){
      val recipe = allRecipes(allRecipes.indexWhere(_.name == name))
      allRecipes -= recipe 
    }
  }
  
  def deleteAllRecipes() = {
    allRecipes = Buffer[Recipe]()
    okToCook = Buffer[Recipe]()
  }
  
  def containsRecipe(name: String): Boolean = {
    allRecipes.exists(_.name == name)
  }
  
  /* 'readFood' saves the data from lines as read from the input file 
   * into the cookbook, if possible. 
   * line(0) should contain the label: either 'raw', 'cooked', or 
   * 'recipe'. If the line is storing a recipe, this method calls 'readRecipe'.
   * If the first label is either 'raw' or 'cooked', then
   * line(1) contains the name of the food, and 
   * line(2) should contain the quantity of the food, given as a number, and
   * line(3) should contain the unit, given as a string, and
   * line(4) might contain the allergen(s).
   * A new food is created only if line(1) contains a number.
   */
  def readFood(line: Array[String]) : Boolean = {
    try {
      if(line(0).trim() == "raw" && Try{line(2).trim().toDouble}.isSuccess) {
        val quantity = new Measure(abs(line(2).trim().toDouble), line(3).trim())
        val food = new Raw(line(1).trim(), quantity)
        if(this.storage.findFood(food.name)==None){
          var all = line(4).trim()
          if (all != "#" && all != ""){
            food.addAllergen(all)
          }
          this.storage.addRaw(food)
          true 
        } else {
          this.storage.findFood(food.name).get.addSome(quantity)
        }
        true
      } else if (line(0).trim() == "cooked"  && Try{line(2).trim().toDouble}.isSuccess) {
        val quantity = new Measure(abs(line(2).trim().toDouble), line(3).trim())
        val food = new Cooked(line(1).trim(), quantity)
        if(this.storage.findFood(food.name)==None){
          var all = line(4).trim().filter(p => p != '#')
          if (all != "#" && all != ""){
            food.addAllergen(line(4).trim()) 
          }
          this.storage.addCooked(food)
          true
        } else {
          this.storage.findFood(food.name).get.addSome(quantity)
        }
        true
      } else if (line(0).trim() == "recipe") { 
        this.readRecipe(line) 
      } else {
        false
      }
    } catch {
       case e:IOException => {println( "Error while reading the file" ); false}
     }
    }
    
  
  /* If line(0) contains the label "recipe", then:
   * - line(1) contains the name of the recipe
   * - line(2) contains the number of ingredients
   * - each ingredients uses 3 slots
   * - line(3) line(4) line(5) will contain the first ingredient
   * 		where line(3) is the name of the ingredient
   * 					line(4) is the quantity (number) required for the recipe
   * 					line(5) is the unit
   * If the recipe is not known, a new recipe is created.
   * For each ingredient, if the storage contains the ingredient (even if with quantity 0),
   * 											the units are checked. The ingredient is saved in the new recipe
   * 											with the same units as in the storage.
   * 											If the storage doesn't know the ingredient, 
   * 											a new Raw ingredient with quantity 0 and same units as in the recipe is created
   * 											and the ingredient is saved in the new recipe.
   * The positive integer in line(2) times 3 says how many fields are taken by ingredients.  
   * By adding the 3 fields (label, name, num. of ingredients), we get the index of next field, 
   * 		which should contain the description. If there is no such field, the description is left empty.
   */
  
  def readRecipe(line: Array[String]) : Boolean = {
    try {
      val recipe = new Recipe(line(1).trim())
      if (!this.containsRecipe(recipe.name)){
        this.addRecipe(recipe)
        if (Try{line(2).trim().toInt}.isSuccess){
          for (i <- 0 until 3*abs(line(2).trim().toInt) by 3){
            var food = Array(line(i+3).trim(), line(i+4).trim(), line(i+5).trim())
            var possibleFood = this.storage.findFood(food(0))
            if (possibleFood.isDefined && possibleFood.get.quantity.unit == food(2) && Try{food(1).toDouble}.isSuccess) {
              val recipeQuantity = new Measure(abs(food(1).toDouble), food(2))
              recipe.saveIngredient(possibleFood.get, recipeQuantity)  
            } else if (possibleFood.isDefined && possibleFood.get.quantity.unit != food(2) && Try{food(1).toDouble}.isSuccess) {
              val quantity = new Measure(abs(food(1).toDouble), food(2))
              val converted = quantity.convert(possibleFood.get.quantity.unit, possibleFood.get.returnDensity, possibleFood.get.returnGramsPerPiece)
              recipe.saveIngredient(possibleFood.get, converted)
            } else {
              val quantityToAdd = new Measure(0.0, food(2))
              val foodToAdd = new Raw(food(0), quantityToAdd)
              this.storage.addRaw(foodToAdd)
              val recipeQuantity = new Measure(abs(food(1).toDouble), food(2))
              recipe.saveIngredient(foodToAdd, recipeQuantity)
            }
          }
        }
        if (Try{line(2).trim().toInt}.isSuccess){
          var index = abs(line(2).trim().toInt)*3 + 3
          if (index < line.length) {
            recipe.addDescription(line(index))
          } 
        }  else {
          recipe.addDescription("") 
        }
        true
      } else { 
        false
        }
    } catch {
       case e:IOException => {println( "Error while reading the file" ); false}
     }
    }

    
  def getRecipe(name: String): Option[Recipe] = {  
      allRecipes.find(p => p.name == name)
  }
  
  
  /* For each recipe, check if can be cooked
   * and collect all recipes that can be cooked in a Buffer. 
   */
  def canCook : Buffer[Recipe] = {
    okToCook = Buffer[Recipe]()
    for (rec <- allRecipes) {
      if (recursiveCanBeCooked(rec)){
        okToCook += rec
        }
      }
      okToCook
    }
  
  
  /* CASE 1: if storage contains enough of all ingredients
   * CASE 2: if storage contains a fraction of all ingredients
   * CASE 3: for all ingredients that are not enough, check if there is a corresponding recipe
   * 				 and check if that recipe can be cooked
   * ELSE: false
   * Warning: this methods does not check against a possible updated version of the storage.
   * This should be added.
   */
  
  def recursiveCanBeCooked(recipe: Recipe) : Boolean = {
    if (recipe.enoughInStorage) true
    else if (recipe.moreThan0)  true 
    else if (!recipe.missingIngredients.isEmpty){
        var ans = Buffer[Boolean]() 
        for (el <- recipe.missingIngredients){
          if (getRecipe(el.name).isDefined){
            ans += recursiveCanBeCooked(getRecipe(el.name).get)
          } else {
            ans += false
          }
        }
        return ans.forall(_ == true)
      }
    else false
  }
  
  

  /* If the recipe can be cooked, 'partialCook' returns the ratio of that 
   * recipe that can be cooked with the saved ingredients. The ratio is 0.0
   * if some ingredient is missing. However, if the recipe can be cooked, 
   * missing ingredients can be cooked. After cooking all missing ingredients, 
   * 'partialCook' will be greater than 0.0 and the recipe itself can be cooked.
   */
  def cook(recipe: Recipe) : Boolean = { 
    if (recursiveCanBeCooked(recipe) && partialCook(recipe) != 0.0){
      val newFood = recipe.cook(partialCook(recipe))
        for (a <- recipe.allergenList) {
          if (!newFood.printAllergen.contains(a)){
            newFood.addAllergen(a)
          }
        }
      storage.addCooked(newFood)
      true
    } else if (recursiveCanBeCooked(recipe) && partialCook(recipe) == 0.0){
      for (el <- recipe.missingIngredients){
          if (getRecipe(el.name).isDefined){
            val subRec = getRecipe(el.name).get
            val cookedFood = subRec.cook(partialCook(subRec))
              for (a <- subRec.allergenList) {
                if (!cookedFood.printAllergen.contains(a)){
                  cookedFood.addAllergen(a)
                }
              }
            storage.addCooked(cookedFood)
          }
      }
      cook(recipe)
    } else {
      false
    }
  }   
    
  
  /* The proportion of a recipe that can be cooked is computed 
   * by comparing the quantities in storage with the required quantities,
   * after performing unit conversion if needed. For example, if a recipe 
   * need 2 liters of milk and 200 grams of chocolate, and the storage has
   * 1 liter of milk and 50 grams of chocolate, the computed ratios are
   * 1/2 and 1/4. The smallest ratio, in this case 1/4, 
   * will be the maximum proportion of recipe that can be cooked.
   * If the smallest ratio is greater than 1.0, the
   * recipe will be cooked simply by following its instructions. 
   * It can be cooked again later.
   * */
  def partialCook(recipe: Recipe) : Double = {  
    var ratios = Map[Food, Double]()
    var ratioToCook = 0.0
    for (el <- recipe.getIngredients){
      if(storage.allFood.contains(el._1)){
        val f = storage.allFood.find(_ == el._1).get
        val q = f.quantity
        if (el._2.unit == q.unit && q.number!=0.0){
          ratios += el._1 -> q.number/el._2.number
        } else if (el._2.unit != q.unit && q.number!=0.0){
          ratios += el._1 -> q.convert(el._2.unit, f.returnDensity, f.returnGramsPerPiece).number/el._2.number
        } else {
          ratios += el._1 -> 0.0
        }
      } else {
          ratios += el._1 -> 0.0
        }
    }
    if (ratios.values.forall(_ != 0.0)) {
      ratioToCook = scala.math.min(ratios.values.min, 1.0)
    }
    ratioToCook
  }
  
  /* 'findPartialName finds all the recipes whose name contains at least 
   * one of the strings given as input. 
   */
  def findPartialName(input: Array[String]) : Buffer[Recipe] = {
    var searchResult = Buffer[Recipe]()
    for (k <- input){
      var keyword = k.trim().toLowerCase()
      for (rec <- allRecipes) {
        if (!keyword.isEmpty() && rec.name.toLowerCase().contains(keyword)) {
          searchResult += rec
        }      
     }
   } 
   searchResult.distinct
  }
  
  /* 'recipesContainingAllIng' finds all the recipes whose ingredients' names 
   * contain all the strings given as input. It correspond to a search with AND.
   */
  def recipesContainingAllIng(input: Array[String]) : Buffer[Recipe] = {
    var searchResult = allRecipes
    for (k <- input){
        var keyword = k.trim().toLowerCase()
        if (!keyword.isEmpty()) {
          searchResult = searchResult.filter(p => p.getIngredients.keySet.map(f => f.name.toLowerCase()).exists(f => f.contains(keyword)))
        }
    }
    if (input.forall(_.trim().isEmpty)) {
      return Buffer[Recipe]()
    } else {
      return searchResult
    }
  }
  
  /* 'recipeWithout filters from the list Buffer given as input 
   * all the recipes that do not contain the given allergen.
   */
  def recipeWithout(list: Buffer[Recipe], allergen: String) : Buffer[Recipe] = {
    var updated = Buffer[Recipe]()
    for (el <- list) {
    if (!el.allergenList.contains(allergen)){
      updated += el
      }
    }
    updated
  }
  
  /* 'nMoreNeeded' selects from all the recipes stored in the cookbook
   * the ones who have n missing ingredients.
   */
  def nMoreNeeded(n: Int) : Buffer[Recipe] = {
    allRecipes.filter(p => p.missingIngredients.length == n)
  }
  
  /* 'recipeWithFoodAndQuantity' searches from list
   * all the recipes that have one ingredient whose name
   * exactly matches foodName and whose required quantity
   * exactly matches quantity.
   */
  def recipeWithFoodAndQuantity(list: Buffer[Recipe], foodName: String, quantity: Measure) : Buffer[Recipe] = {
    if(quantity.number <= 0.0) Buffer[Recipe]()
    else list.filter(p => p.hasIngredientAndQuantity(foodName, quantity))
  }
  
  
}