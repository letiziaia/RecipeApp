package cookbook

import scala.collection.mutable.Buffer


class Storage (val name: String) {
  
  var allFood = Buffer[Food]()
  var allRaw = Buffer[Raw]()
  var allCooked = Buffer[Cooked]()
  
  
  /* 'addRaw' adds to the storage the raw food given as parameter.
   * If that food already exists, it sums the quantities. */  
  def addRaw(newFood: Raw) = {
    if(allRaw.find(p => p.name == newFood.name).isDefined){
      allRaw.find(p => p.name == newFood.name).get.addSome(newFood.quantity)
    } else {
      allRaw += newFood
      allFood += newFood
    }
  }
  
  
  /* 'addCooked' adds to the storage the cooked food given as parameter.
   * If that food already exists, it sums the quantities. */  
  def addCooked(newFood: Cooked) = {
    if(allCooked.find(p => p.name == newFood.name).isDefined){
      allCooked.find(p => p.name == newFood.name).get.addSome(newFood.quantity)
    } else {
      allCooked += newFood
      allFood += newFood
    }
  }
  
  
  /* 'deleteFood' deletes from the storage the food given as input */
  def deleteFood(food: Food) : Boolean = {
    if (allCooked.contains(food)){
      val remFood = allCooked(allCooked.indexWhere(_.name == food.name))
      allCooked -= remFood
      allFood -= remFood
      true
    } 
    else if (allRaw.contains(food)){
      val remFood = allRaw(allRaw.indexWhere(_.name == food.name))
      allRaw -= remFood
      allFood -= remFood 
      true
    }
    else {
      false
    }
  }
  
  
  /* 'deleteAllFood()' deletes evertything from the storage */
  def deleteAllFood() = {
    allRaw = Buffer[Raw]()
    allCooked = Buffer[Cooked]()
    allFood = Buffer[Food]()
  }
  
  
  /* 'findFood()' tries to find a food that has the name given as parameter */
  def findFood(name: String) : Option[Food] = {
    allFood.find(p => p.name == name)
  }
  
  
  /* 'listContent' list all the food in storage */
  def listContent : Buffer[Food] = allFood
  
  
  /* 'findPartial' collects all foods whose name contains 
   * one of the element in the array as substring.
   */
  def findPartial(string: Array[String]) : Buffer[Food] = {
    var searchResult = Buffer[Food]()
    for (k <- string){
      var keyword = k.trim().toLowerCase()
      for (fo <- allFood) {
        if (!keyword.isEmpty() && fo.name.toLowerCase().contains(keyword)) {
          searchResult += fo
        } 
       }
      }
   searchResult.distinct
  }
  
  
  /* 'foodWithout' filters from the list given as input 
   * all foods that do not contain the given allergen.
   */
  def foodWithout(list: Buffer[Food], allergen: String) : Buffer[Food] = {
    var updated = Buffer[Food]()
    for (el <- list) {
      if (!el.printAllergen.contains(allergen)){
        updated += el
      } 
    }
    updated.distinct
  }
  
  
  /* 'rawWithout' filters from the list given as input 
   * all raw foods that do not contain the given allergen.
   */  
  def rawWithout(list: Buffer[Raw], allergen: String) : Buffer[Raw] = {
    var updated = Buffer[Raw]()
    for (el <- list) {
      if (!el.printAllergen.contains(allergen)){
        updated += el
      } 
    }
    updated.distinct
  }
  
  
  /* 'cookedWithout' filters from the list given as input 
   * all cooked foods that do not contain the given allergen.
   */  
  def cookedWithout(list: Buffer[Cooked], allergen: String) : Buffer[Cooked] = {
    var updated = Buffer[Cooked]()
    for (el <- list) {
      if (!el.printAllergen.contains(allergen)){
        updated += el
      } 
    }
    updated.distinct
  }
}