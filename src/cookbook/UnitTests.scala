package cookbook

import cookbook._

import org.junit.Test
import org.junit.Assert._



class UnitTests {

@Test def CanBeCooked1() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q150)
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 1.0))
    assertTrue(cookbook.cook(recipe) && cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 150.0)
    assertTrue(food.quantity.number == 0.0)
}

@Test def CanBeCooked2() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q100)
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 2.0/3))
    assertTrue(cookbook.cook(recipe) && 
        cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == (150.0)*2.0/3)
    assertTrue(food.quantity.number == 0.0)
}

@Test def CanBeCooked3() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q150)
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q100)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 1.0))
    assertTrue(cookbook.cook(recipe) && 
        cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 100.0)
    assertTrue(food.quantity.number == 50.0)
}

@Test def CanBeCooked4() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food1 = new Raw("bread", q100)
    val food2 = new Raw("milk", q150)
    val food3 = new Raw("tomato", q100)
    cookbook.storage.addRaw(food1)
    cookbook.storage.addRaw(food2)
    cookbook.storage.addRaw(food3)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food1, q100)
    recipe.saveIngredient(food2, q150)
    recipe.saveIngredient(food3, q150)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 2.0/3))
    assertTrue(cookbook.cook(recipe))
}

@Test def CanBeCooked5() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food1 = new Cooked("bread", q100)
    val food2 = new Raw("milk", q150)
    val food3 = new Cooked("tomato", q100)
    cookbook.storage.addCooked(food1)
    cookbook.storage.addRaw(food2)
    cookbook.storage.addCooked(food3)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food1, q100)
    recipe.saveIngredient(food2, q150)
    recipe.saveIngredient(food3, q150)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 2.0/3))
    assertTrue(cookbook.cook(recipe))
}

@Test def CanBeCooked6() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val q0 = new Measure(0.0, "grams")
    val food1 = new Cooked("bread", q100)
    val food2 = new Raw("milk", q150)
    val food3 = new Cooked("tomato", q100)
    val food4 = new Cooked("dough", q0)
    cookbook.storage.addCooked(food1)
    cookbook.storage.addRaw(food2)
    cookbook.storage.addCooked(food3)
    cookbook.storage.addCooked(food4)
    val recipe2 = new Recipe("dough")
    recipe2.saveIngredient(food2, q150)
    recipe2.saveIngredient(food3, q100)
    val recipe1 = new Recipe("recipe")
    recipe1.saveIngredient(food1, q100)
    recipe1.saveIngredient(food4, q150)
    cookbook.addRecipe(recipe1)
    cookbook.addRecipe(recipe2)
    assertTrue(cookbook.recursiveCanBeCooked(recipe2))
    assertTrue(cookbook.partialCook(recipe2) == 1.0)
    assertTrue(cookbook.cook(recipe1) &&
        cookbook.storage.allCooked.find(p => p.name == recipe1.name).get.quantity.number == (150.0+100.0))
    assertTrue(food2.quantity.number == 0.0)
}

@Test def CanBeCooked7() {
    val cookbook = new Cookbook
    val q300 = new Measure(300.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q300)
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
//    println(cookbook.storage.allFood.map(f => f.name).mkString(" "))
//    println(cookbook.storage.allFood.map(f => f.quantity.number).mkString(" "))
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 1.0))
    assertTrue(cookbook.cook(recipe) && cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 150.0)
//    println(cookbook.storage.allFood.map(f => f.name).mkString(" "))
//    println(cookbook.storage.allFood.map(f => f.quantity.number).mkString(" "))
    assertTrue(cookbook.cook(recipe) && cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 300.0)
//    println(cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number)
//    println(cookbook.storage.allFood.map(f => f.name).mkString(" "))
//    println(cookbook.storage.allFood.map(f => f.quantity.number).mkString(" "))
}


@Test def CanBeCooked8() {
    val cookbook = new Cookbook
    val q250 = new Measure(250.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q250)
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(cookbook.recursiveCanBeCooked(recipe) && (cookbook.partialCook(recipe) == 1.0))
    assertTrue(cookbook.cook(recipe) && cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 150.0)
    assertTrue(cookbook.cook(recipe) && cookbook.storage.allCooked.find(p => p.name == recipe.name).get.quantity.number == 250.0)
}

@Test def CanBeCooked9() {
    val cookbook = new Cookbook
    val q50 = new Measure(50.0, "grams")
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "grams")
    val q0 = new Measure(0.0, "grams")
    val food1 = new Cooked("bread", q100)
    val food2 = new Raw("milk", q50)
    val food3 = new Cooked("tomato", q50)
    val food4 = new Cooked("dough", q0)
    cookbook.storage.addCooked(food1)
    cookbook.storage.addRaw(food2)
    cookbook.storage.addCooked(food3)
    cookbook.storage.addCooked(food4)
    val recipe2 = new Recipe("dough")
    recipe2.saveIngredient(food2, q100)
    recipe2.saveIngredient(food3, q100)
    val recipe1 = new Recipe("recipe")
    recipe1.saveIngredient(food1, q100)
    recipe1.saveIngredient(food4, q150)
    cookbook.addRecipe(recipe1)
    cookbook.addRecipe(recipe2)
    assertTrue(cookbook.recursiveCanBeCooked(recipe2))
    assertTrue(cookbook.partialCook(recipe2) == 0.5)
    assertTrue(cookbook.cook(recipe1) )
    assertTrue(food2.quantity.number == 0.0)
}

@Test def ConvertWeight1() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "grams")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("kg", food.returnDensity, food.returnGramsPerPiece).number == 1.0)
    assertTrue(food.quantity.unit == "kg")
}

@Test def ConvertWeight2() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "grams")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("mg", food.returnDensity, food.returnGramsPerPiece).number == 1000000.0)
    assertTrue(food.quantity.unit == "mg")
}

@Test def ConvertWeight3() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "mg")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "mg")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 1.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertVolume1() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "liters")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("kl", food.returnDensity, food.returnGramsPerPiece).number == 1.0)
    assertTrue(food.quantity.unit == "kl")
}

@Test def ConvertVolume2() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "liters")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("ml", food.returnDensity, food.returnGramsPerPiece).number == 1000000.0)
    assertTrue(food.quantity.unit == "ml")
}

@Test def ConvertVolume3() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "ml")
    val food = new Cooked("bread", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "ml")
    assertTrue(food.quantity.convert("liters", food.returnDensity, food.returnGramsPerPiece).number == 1.0)
    assertTrue(food.quantity.unit == "liters")
}


@Test def ConvertVolumeToWeight1() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "liters")
    val food = new Cooked("milk", q1000)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("kg", food.returnDensity, food.returnGramsPerPiece).number == 1000.0)
    assertTrue(food.quantity.unit == "kg")
}

@Test def ConvertVolumeToWeight2() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "dal")
    val food = new Cooked("milk", q10)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "dal")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 100000.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertVolumeToCups1() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "dal")
    val food = new Cooked("milk", q10)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "dal")
    assertTrue(food.quantity.convert("cups", food.returnDensity, food.returnGramsPerPiece).number == 100000.0/240.0)
    assertTrue(food.quantity.unit == "cups")
}

@Test def ConvertCupsToWeight() {
    val cookbook = new Cookbook
    val q2 = new Measure(2.0, "cups")
    val food = new Cooked("milk", q2)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "cups")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 2.0*240.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertCupsToVolume() {
    val cookbook = new Cookbook
    val q2 = new Measure(2.0, "cups")
    val food = new Cooked("milk", q2)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "cups")
    assertTrue(food.quantity.convert("liters", food.returnDensity, food.returnGramsPerPiece).number == 2.0*240.0/1000)
    assertTrue(food.quantity.unit == "liters")
}

@Test def ConvertWeightToVolume() {
    val cookbook = new Cookbook
    val q2 = new Measure(200.0, "grams")
    val food = new Cooked("milk", q2)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("liters", food.returnDensity, food.returnGramsPerPiece).number == 0.2)
    assertTrue(food.quantity.unit == "liters")
}

@Test def ConvertWeightToCups() {
    val cookbook = new Cookbook
    val q2 = new Measure(200.0, "grams")
    val food = new Cooked("milk", q2)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("cups", food.returnDensity, food.returnGramsPerPiece).number == 200.0/240.0)
    assertTrue(food.quantity.unit == "cups")
}

@Test def ConvertWeightToPieces() {
    val cookbook = new Cookbook
    val q2 = new Measure(200.0, "grams")
    val food = new Cooked("bread", q2)
    food.setGramsPerPiece(50.0)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("pieces", food.returnDensity, food.returnGramsPerPiece).number == 200.0/50.0)
    assertTrue(food.quantity.unit == "pieces")
}

@Test def ConvertPiecesToWeight() {
    val cookbook = new Cookbook
    val q2 = new Measure(6, "pieces")
    val food = new Cooked("eggs", q2)
    food.setGramsPerPiece(50.0)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "pieces")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 6*50.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertVolumeToWeightWithDensity1() {
    val cookbook = new Cookbook
    val q1000 = new Measure(1000.0, "liters")
    val food = new Cooked("milk", q1000)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("kg", food.returnDensity, food.returnGramsPerPiece).number == 1.029*1000.0)
    assertTrue(food.quantity.unit == "kg")
}

@Test def ConvertVolumeToWeightWithDensity2() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "dal")
    val food = new Cooked("milk", q10)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "dal")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 1.029*100000.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertVolumeToCups1WithDensity() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "liters")
    val food = new Cooked("milk", q10)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("cups", food.returnDensity, food.returnGramsPerPiece).number == 10000.0/240.0)
    assertTrue(food.quantity.unit == "cups")
}

@Test def ConvertCupsToWeightWithDensity() {
    val cookbook = new Cookbook
    val q2 = new Measure(2.0, "cups")
    val food = new Cooked("milk", q2)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "cups")
    assertTrue(food.quantity.convert("grams", food.returnDensity, food.returnGramsPerPiece).number == 1.029*2.0*240.0)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertCupsToVolumeWithDensity() {
    val cookbook = new Cookbook
    val q2 = new Measure(2.0, "cups")
    val food = new Cooked("milk", q2)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "cups")
    assertTrue(food.quantity.convert("liters", food.returnDensity, food.returnGramsPerPiece).number == 2.0*240.0/1000)
    assertTrue(food.quantity.unit == "liters")
}

@Test def ConvertWeightToVolumeWithDensity() {
    val cookbook = new Cookbook
    val q2 = new Measure(200.0, "grams")
    val food = new Cooked("milk", q2)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("liters", food.returnDensity, food.returnGramsPerPiece).number == 0.2/1.029)
    assertTrue(food.quantity.unit == "liters")
}

@Test def ConvertWeightToCupsWithDensity() {
    val cookbook = new Cookbook
    val q2 = new Measure(200.0, "grams")
    val food = new Cooked("milk", q2)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "grams")
    assertTrue(food.quantity.convert("cups", food.returnDensity, food.returnGramsPerPiece).number == 200.0/(1.029*240.0))
    assertTrue(food.quantity.unit == "cups")
}

@Test def ConvertVolumeToPieces() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "liters")
    val food = new Cooked("milk", q10)
    food.setDensity(1.029)
    food.setGramsPerPiece(150.0)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("pieces", food.returnDensity, food.returnGramsPerPiece).number == 1.029*10000/150.0)
    assertTrue(food.quantity.unit == "pieces")
}

@Test def ConvertVolumeToPiecesWithoutGPP() {
    val cookbook = new Cookbook
    val q10 = new Measure(10.0, "liters")
    val food = new Cooked("milk", q10)
    food.setDensity(1.029)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "liters")
    assertTrue(food.quantity.convert("pieces", food.returnDensity, food.returnGramsPerPiece).number == 1.029*10000)
    assertTrue(food.quantity.unit == "grams")
}

@Test def ConvertPiecesToVolume() {
    val cookbook = new Cookbook
    val q2 = new Measure(6, "pieces")
    val food = new Cooked("eggs", q2)
    food.setDensity(1.031)
    food.setGramsPerPiece(50.0)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "pieces")
    assertTrue(food.quantity.convert("ml", food.returnDensity, food.returnGramsPerPiece).number == 6*50.0/1.031)
    assertTrue(food.quantity.unit == "ml")
}

@Test def ConvertPiecesToVolumeWithoutGPP() {
    val cookbook = new Cookbook
    val q2 = new Measure(6, "pieces")
    val food = new Cooked("eggs", q2)
    food.setDensity(1.031)
    cookbook.storage.addCooked(food)
    assertTrue(food.quantity.unit == "pieces")
    assertTrue(food.quantity.convert("ml", food.returnDensity, food.returnGramsPerPiece).number == 6)
    assertTrue(food.quantity.unit == "pieces")
}

@Test def FindRecipePartialName() {
    val cookbook = new Cookbook
    val recipe1 = new Recipe("abcdgggef") //in
    val recipe2 = new Recipe("gggef") //in
    val recipe3 = new Recipe("abcdggg") //in
    val recipe4 = new Recipe("abcdggef") //in
    val recipe5 = new Recipe("ggef") //in
    val recipe6 = new Recipe("abcdgg") //in
    val recipe7 = new Recipe("abcdef") //out
    val recipe8 = new Recipe("ghij") //out
    val recipe9 = new Recipe("klm") //in
    cookbook.addRecipe(recipe1)
    cookbook.addRecipe(recipe2)
    cookbook.addRecipe(recipe3)
    cookbook.addRecipe(recipe4)
    cookbook.addRecipe(recipe5)
    cookbook.addRecipe(recipe6)
    cookbook.addRecipe(recipe7)
    cookbook.addRecipe(recipe8)
    cookbook.addRecipe(recipe9)
    val input = Array[String]("gg", "l")
    assertTrue(cookbook.findPartialName(input).size == 7 && 
                cookbook.findPartialName(input).map(f => f.name).contains("klm") && 
                  cookbook.findPartialName(input).map(f => f.name).contains("gggef"))
}

@Test def FindRecipeByIngredientAndQuantity() {
    val cookbook = new Cookbook
    val recipe = new Recipe("recipe")
    val recipe1 = new Recipe("abcdgggef") 
    val recipe2 = new Recipe("gggef") 
    val recipe3 = new Recipe("abcdggg") 
    val recipe4 = new Recipe("abcdggef") 
    val recipe5 = new Recipe("ggef") 
    val recipe6 = new Recipe("abcdgg") 
    val recipe7 = new Recipe("abcdef") 
    val recipe8 = new Recipe("ghij") 
    val recipe9 = new Recipe("klm") 
    val q150 = new Measure(150.0, "grams")
    val q10 = new Measure(10.0, "dl")
    val q = new Measure(150.0, "liters")
    val food = new Raw("food", q150)
    cookbook.storage.addRaw(food)
    recipe.saveIngredient(food, q150)
    recipe5.saveIngredient(food, q150)
    recipe7.saveIngredient(food, q10)
    recipe9.saveIngredient(food, q)
    cookbook.addRecipe(recipe)
    cookbook.addRecipe(recipe1)
    cookbook.addRecipe(recipe2)
    cookbook.addRecipe(recipe3)
    cookbook.addRecipe(recipe4)
    cookbook.addRecipe(recipe5)
    cookbook.addRecipe(recipe6)
    cookbook.addRecipe(recipe7)
    cookbook.addRecipe(recipe8)
    cookbook.addRecipe(recipe9)
    val input = Array[String]("gg", "l")
    assertTrue(cookbook.recipeWithFoodAndQuantity(cookbook.allRecipes, "food", q150).size == 2 &&
                cookbook.recipeWithFoodAndQuantity(cookbook.allRecipes, "food", q150).map(f => f.name).contains("recipe") && 
                  cookbook.recipeWithFoodAndQuantity(cookbook.allRecipes, "food", q150).map(f => f.name).contains("ggef") && 
                    !cookbook.recipeWithFoodAndQuantity(cookbook.allRecipes, "food", q150).map(f => f.name).contains("klm"))
}


@Test def FindRecipePartialIngredientName() {
    val cookbook = new Cookbook
    val q100 = new Measure(100.0, "grams")
    val q150 = new Measure(150.0, "dl")
    val food1 = new Cooked("bread", q100)
    val food2 = new Raw("milk", q150)
    val food3 = new Cooked("tomato", q100)
    val food4 = new Cooked("eggs", q100)
    val food5 = new Raw("water", q150)
    val food6 = new Cooked("eggplants", q100)
    cookbook.storage.addCooked(food1)
    cookbook.storage.addRaw(food2)
    cookbook.storage.addCooked(food3)   
    cookbook.storage.addCooked(food4)
    cookbook.storage.addRaw(food5)
    cookbook.storage.addCooked(food6)
    val recipe1 = new Recipe("r1") 
    val recipe2 = new Recipe("r2") 
    val recipe3 = new Recipe("r3") 
    val recipe4 = new Recipe("r4") 
    val recipe5 = new Recipe("r5") 
    val recipe6 = new Recipe("r6") 
    val recipe7 = new Recipe("r7") 
    val recipe8 = new Recipe("r8") 
    val recipe9 = new Recipe("r9") 
    recipe1.saveIngredient(food1, q150) 
    recipe1.saveIngredient(food2, q150) 
    recipe2.saveIngredient(food1, q150) 
    recipe2.saveIngredient(food2, q150) 
    recipe2.saveIngredient(food3, q150) 
    recipe2.saveIngredient(food4, q150)
    recipe3.saveIngredient(food5, q150) 
    recipe4.saveIngredient(food6, q150)    
    recipe5.saveIngredient(food2, q150)  
    recipe5.saveIngredient(food3, q150) 
    recipe6.saveIngredient(food4, q150)
    recipe7.saveIngredient(food5, q150) 
    recipe8.saveIngredient(food6, q150) 
    recipe9.saveIngredient(food1, q150) 
    cookbook.addRecipe(recipe1)
    cookbook.addRecipe(recipe2)
    cookbook.addRecipe(recipe3)
    cookbook.addRecipe(recipe4)
    cookbook.addRecipe(recipe5)
    cookbook.addRecipe(recipe6)
    cookbook.addRecipe(recipe7)
    cookbook.addRecipe(recipe8)
    cookbook.addRecipe(recipe9)
    val input = Array[String]("a")
    val input1 = Array[String]("bread", "milk")
    assertTrue(cookbook.recipesContainingAllIng(input).size == 8 && 
                !cookbook.recipesContainingAllIng(input).map(f => f.name).contains("r6") && 
                  cookbook.recipesContainingAllIng(input1).size == 2 &&
                    cookbook.recipesContainingAllIng(input1).map(f => f.name).contains("r2"))
}

@Test def RecipeAllergens1() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q150)
    food.addAllergen("G")
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(recipe.allergenList.contains("G"))
}

@Test def RecipeAllergens2() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q150)
    food.addAllergen("L")
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(recipe.allergenList.contains("L"))
}

@Test def RecipeAllergens3() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val food = new Raw("bread", q150)
    food.addAllergen("E")
    cookbook.storage.addRaw(food)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food, q150)
    assertTrue(recipe.allergenList.contains("E"))
}

@Test def RecipeFilterOutAllergens() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val food1 = new Raw("1", q150)
    food1.addAllergen("E")
    cookbook.storage.addRaw(food1)
    val food2 = new Raw("2", q150)
    food2.addAllergen("G")
    cookbook.storage.addRaw(food2)
    val food3 = new Raw("3", q150)
    food3.addAllergen("L")
    cookbook.storage.addRaw(food3)
    val food4 = new Raw("4", q150)
    food4.addAllergen("N")
    cookbook.storage.addRaw(food4)
    val food5 = new Raw("5", q150)
    food5.addAllergen("F")
    cookbook.storage.addRaw(food5)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food1, q150)
    recipe.saveIngredient(food2, q150)
    recipe.saveIngredient(food3, q150)
    recipe.saveIngredient(food4, q150)
    recipe.saveIngredient(food5, q150)
    assertTrue(cookbook.recipeWithout(cookbook.allRecipes, "G").size == 0)
    assertTrue(cookbook.recipeWithout(cookbook.allRecipes, "L").size == 0)
    assertTrue(cookbook.recipeWithout(cookbook.allRecipes, "E").size == 0)
    assertTrue(cookbook.recipeWithout(cookbook.allRecipes, "N").size == 0)
    assertTrue(cookbook.recipeWithout(cookbook.allRecipes, "F").size == 0)
}

@Test def RecipeMissingIngredients() {
    val cookbook = new Cookbook
    val q150 = new Measure(150.0, "grams")
    val q0 = new Measure(0.0, "ml")
    val food1 = new Raw("food1", q150)
    val food2 = new Raw("food2", q0)
    food1.addAllergen("G")
    food2.addAllergen("G")
    cookbook.storage.addRaw(food1)
    cookbook.storage.addRaw(food2)
    val recipe = new Recipe("recipe")
    recipe.saveIngredient(food1, q150)
    recipe.saveIngredient(food2, q150)
    assertTrue(recipe.allergenList.size == 1 && 
                  recipe.missingIngredients.size == 1 &&
                    recipe.missingIngredients.map(f => f.name).contains("food2"))
}
}