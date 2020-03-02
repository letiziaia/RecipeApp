package cookbook.gui

import scala.collection.mutable.Buffer

import cookbook._
import scala.swing._
import java.io._
import javax.swing.plaf.SeparatorUI
import scala.util.Try


/* This is the GUI interface of the Cookbook.
 * It has a menu, from which the user can load information from a text file.
 * The GUI also works as a text editor: from the menu, the text file can be opened and saved.
 * All the editing happens in the text area.
 * The GUI allows users to perform searches by ingredient, by name, by quantity, and excluding allergenes. 
 * It can also list all raw food, all ready food, all recipes, all recipes that can be cooked, 
 * all recipes that need 1, 2, or 3 additional ingredients.
 * The user can edit or create new foods and recipes, perform unit conversions, and cook.
 */


object CookbookApp extends SimpleSwingApplication{ 
 
  val cookbook = new Cookbook
  
  /* List of foods and recipes for left panel */
  val list = new ListView(Seq[String]())
      list.background_=(new java.awt.Color(242, 247, 248))
  
  /* See 'popWindow' */
  var window : Frame = new Frame(){visible = false}
  
  
  /* The method 'importData' imports all the informations saved in the text file
   * into the program. This is a necessary step in order to be able
   * to use the program. The method 'readFood' in the class Cookbook
   * will match every line of the file and create instances. 
   */

  def importData()={
    cookbook.storage.deleteAllFood()
    cookbook.deleteAllRecipes()
    
    val fileChooser = new FileChooser
    if (fileChooser.showOpenDialog(null)==FileChooser.Result.Approve){
     val fileReader = new FileReader(fileChooser.selectedFile)
     val readLine = new BufferedReader(fileReader)
     
     try {
       var input = readLine.readLine()
       while( input != null ) {
         val info = input.split('$')
         cookbook.readFood(info)
         input = readLine.readLine()
       }
     }
     catch {
       case e:IOException => println( "Error while reading the file" )
     }
    }
    
    list.listData = buildAllList
    list.repaint() 
  }
  
  
  /* The method 'saveData' exports all the informations in a new text file.
   * The format allows the text file to be read again by the Cookbook. 
   */  
  def saveData() = {
    val fileChooser = new FileChooser
    if (fileChooser.showSaveDialog(null)==FileChooser.Result.Approve) {
      val file = new FileWriter(fileChooser.selectedFile)
      val fileWriter = new PrintWriter(file)
      for(food <- cookbook.storage.allRaw){
        fileWriter.write("raw$" + food.name + "$" + food.quantity.number + "$" + 
                          food.quantity.unit + "$" + food.printAllergen + "$# ")
        fileWriter.write("\n")
      }
      for(food <- cookbook.storage.allCooked){
        fileWriter.write("cooked$" + food.name + "$" + food.quantity.number + "$" + 
                          food.quantity.unit + "$" + food.printAllergen.mkString(" ") + "$# ")
        fileWriter.write("\n")
      }
      for (recipe <- cookbook.allRecipes){
        fileWriter.write("recipe$" + recipe.name + "$" + recipe.getIngredients.size + "$")
        for(ing <- recipe.getIngredients) {
          fileWriter.write(ing._1.name + "$" + ing._2.number + "$" + ing._2.unit + "$")
        }
        fileWriter.write(recipe.printDescription + "$# ")
        fileWriter.write("\n")
      }
      fileWriter.close()
    }
  }
  
  
  
  /* textArea shows the text file in text editor mode. 
   * It is grouped inside panel, that has also the save button. */
  val textArea = new TextArea {border = thinBlackBorder; lineWrap = true; wordWrap = true} 
  val saveButton = new Button("Save file")
  val editView = new BorderPanel{
    layout+=new ScrollPane(textArea) -> BorderPanel.Position.Center
    layout+=saveButton -> BorderPanel.Position.South
  }
  
  val editViewBackground = new BorderPanel{
    layout += editView -> BorderPanel.Position.Center
    background = java.awt.Color.lightGray
    border = Swing.EmptyBorder(20, 20, 10, 20)
  }
  
  
    
  /* The method 'openFile' opens the text file in the text area, 
   * so that it the GUI works as a text editor and the file
   * can be read and edited. Primal use: debug and testing.
   */
  def openFile()={
    val chooser = new FileChooser
    if (chooser.showOpenDialog(null)==FileChooser.Result.Approve){
      val input = scala.io.Source.fromFile(chooser.selectedFile)
      newPanel.layout(editViewBackground) = BorderPanel.Position.Center
      textArea.text = input.mkString
      newPanel.repaint()
      newPanel.revalidate()
      input.close()
    }
  }


  
  /* The method 'saveFile' allows users to save their edits to the text file,
   * when using the GUI as text editor. Changes can be saved either in a new file or in the same file. 
   * However, edits are not authomatically loaded after saving the file: 'Import Data' is necessary.
   */
  def saveFile()={
    val chooser = new FileChooser
    if (chooser.showSaveDialog(null)==FileChooser.Result.Approve){
     val save = new java.io.PrintWriter(chooser.selectedFile)
     save.print(textArea.text)
     save.close()
    }
  }
  
  
  /* Radio buttons that represent units.
   * However, because of the space, only 
   * 'pieces', 'cups', 'liters', 'grams', 'kg', and 'dl' 
   * are now shown in the GUI. 
   */
    val pieces = new RadioButton("pieces	")
    val cups = new RadioButton("cups	")
    val kg = new RadioButton("kg	")
    val hg = new RadioButton("hg	")
    val dag = new RadioButton("dag	")
    val grams = new RadioButton("grams	")
    val dg = new RadioButton("dg	")
    val cg = new RadioButton("cg	")
    val mg = new RadioButton("mg	")
    val kl = new RadioButton("kl	")
    val hl = new RadioButton("hl	")
    val dal = new RadioButton("dal	")
    val liters = new RadioButton("liters	")
    val dl = new RadioButton("dl	")
    val cl = new RadioButton("cl	")
    val ml = new RadioButton("ml	")
    
    val unitgroup = new ButtonGroup
    val radios = List(cups, kg, grams, liters, dl, pieces)
    unitgroup.buttons ++= radios    
    
    
  /* 'unitButtons' shows the units in the top part of the GUI,
   * from where they can be selected and used for quantitySearch
   */
    val unitButtons = new BoxPanel(Orientation.Horizontal) {
      contents ++= radios
    }
  
    
    
  /* The GUI window consists of:
   * - one main window          |
   * - a menu with 4 items  		| -> those two in MainFrame 'top' 
   * - a line with a Label, a text field, and the button 'Find', grouped in 'splitTop'     | 
   * - a line with allergenes that can be thicked to exclude them from the search,         | 
   * 	 a Label, a TextArea, and 'unitButtons' for 'quantitySearch', grouped in 'splitDown' | -> those two in 'userDef'
   * - a drop-down menu for listing all raw food, or all ready food, or all recipes -> see 'dropdown'
   * - a bigger panel that serves also as text editor -> see 'newPanel'
   */

  val newPanel = new BorderPanel{}
      newPanel.layoutManager.setVgap(5)
      newPanel.layoutManager.setHgap(10)
  
  /* Borders */
  val thinBlackBorder = Swing.LineBorder(java.awt.Color.DARK_GRAY, 1)
  val redBorder = Swing.LineBorder(java.awt.Color.red, 2)
  val greenBorder = Swing.LineBorder(java.awt.Color.green, 2)
  val orangeBorder = Swing.LineBorder(java.awt.Color.orange, 2)
  val cyBorder = Swing.LineBorder(java.awt.Color.cyan, 2)
  val grayBorder = Swing.LineBorder(java.awt.Color.DARK_GRAY, 2)
  
    
  /* Dropdown menu for left panel */
  val dropdown = new ComboBox(List(" - Or select here to list all", 
                                    "Raw Ingredients", 
                                    "Ready Food", 
                                    "Recipes", 
                                    "What I can cook",
                                    "1 ingredient missing",
                                    "2 ingredients missing",
                                    "3 ingredients missing"))
   
         
  /* 'userDef' takes care of user-defined searches.
   * It consists of 2 lines:
   * - 'splitTop' holds the first line
   * - 'splitDown' holds the second line
   * Users can write in 'textSearch',
   * tick checkboxes 'check_' to exclude allergens from the search results,
   * write a quantity in 'quantSearchField',
   * select a unit from 'unitButtons',
   * and finally press 'findButton'.
   * Search results will appear in 'newPanel',
   * that will be turned into 'searchView'.
   */
  val userDef = new BorderPanel{  
        val textSearch = new TextArea {rows = 2; columns = 50; 
                                       lineWrap = true; wordWrap = true; 
                                       }
        
        val findButton = Button("Find"){}
          findButton.border_=(grayBorder)
          findButton.peer.setPreferredSize(new Dimension(90, 40))
        
        val search = new BoxPanel(Orientation.Horizontal){
          contents += new Label("Write an ingredient or a recipe:    ")
          contents += textSearch
        }
        val splitTop = new SplitPane(Orientation.Vertical, search, findButton) {continuousLayout = true}

        val box = new BoxPanel(Orientation.Horizontal){
          contents += new Label("Tick to exclude:     ")
          val checkL = new CheckBox("Lactose (L)  "){}
          val checkG = new CheckBox("Gluten (G)  "){}
          val checkE = new CheckBox("Eggs (E)  "){}
          val checkN = new CheckBox("Peanuts (N)  "){}
          val checkF = new CheckBox("Fish (F)  "){}
          contents += checkL
          contents += checkG
          contents += checkE
          contents += checkN
          contents += checkF
        }
        
        val quantSearchField = new TextField { columns = 15; minimumSize = new Dimension(40, 10) }
        
        val quantitySearch = new BoxPanel(Orientation.Horizontal){
          contents += new Label("Write quantity:     ")
          contents += quantSearchField
          contents += unitButtons
        }
        val splitDown = new SplitPane(Orientation.Vertical, box, quantitySearch) {continuousLayout = true}

        layout += splitTop -> BorderPanel.Position.Center
        layout += splitDown -> BorderPanel.Position.South
      }
    
  /***********************************************************************************************/
  /* SWING TOP - GUI
   * This is the main window of the GUI. It contains:
   * - a title
   * - a menu 'menuBar' with 4 options:
   * 		'importData': load the data into the program
   * 		'openFile': open text file in edit mode
   * 		'popUpInfos()': open a new window with directions on how to use the program
   * 		'sys.exit': closes the program
   * - 'newPanel', which holds:
   * 			'userDef': the first two lines, made by 'splitTop' and 'splitDown'
   * 			'dropdown': the dropdown menu
   * 			'list': the complete list displayed on the left
   * The central part of 'newPanel' can be replaced by foodView, recipeView, searchView, or editView. 
   */
  /***********************************************************************************************/
  val top = new MainFrame(){
  
    title = "My Smart Cookbook"
    
    menuBar = new MenuBar{
      contents += new Menu("Menu"){
        contents += new MenuItem(Action("Import Data"){ importData })
        contents += new MenuItem(Action("Export Data"){ saveData })
        contents += new Separator
        contents += new MenuItem(Action("Open File"){ openFile })
        contents += new Separator
        contents += new MenuItem(Action("How this works"){ popUpInfos() })
        contents += new MenuItem(Action("Exit"){ sys.exit(0) })
      }
    }
    
    val innerPanel = new BorderPanel {
      layout += dropdown -> BorderPanel.Position.North
      layout += list -> BorderPanel.Position.Center
      }
    
    innerPanel.peer.setBorder(thinBlackBorder)
     
    contents = new ScrollPane(newPanel)
      newPanel.layout(userDef) = BorderPanel.Position.North
      newPanel.layout(innerPanel) = BorderPanel.Position.West    
      
    size = new Dimension(1150,650)
    visible = true
    centerOnScreen
  }

   
  
  /* FOOD VIEW
   * This part displays details of food items in 'newPanel':
   * - 'foodName': name of the food
   * - 'foodQuantity': quantity and unit of the stored food
   * - 'allergen': all the known allegerns of the food
   * - 'density': density of the food, used for unit conversion -> see class Measure
   * 							NB: When recipes have unknown ingredients, Cookbook will create new Food objects
   * 									with the name of the unknown food and default values, such as 0.0 grams for 
   * 									quantity, 1.0 for density, and no allergen. 
   * 									When foods are loaded from the text file, a default value of 1.0 is given
   * 									for density.
   * - 'gramsPerPiece': the value of gramsPerPiece of the food -> see class Food and class Measure
   * 										NB: The default value of gramsPerPiece inside food is None, which is 
   * 										shown in the GUI as N/D. 
   * Details are displayed in textFields, where the user can also type.
   * Buttons allow the following operations:
   * - 'newFood': save the information written in textFields as a new instance of Raw Food, 
   * 							if the name is different from the already existing foods.
   * - 'edit': edit the already existing food by saving the current informations in textFields,
   * 					if the name corresponds to some already existing food.
   * - 'deleteFood': delete the food from the cookbook
   * - 'cancel': clean all the text fields, but nothing happens to data
   */

  
  val foodName = new TextField {border = thinBlackBorder}
  val foodQuantity = new TextField {border = thinBlackBorder}
  val allergen = new TextField {border = thinBlackBorder}
  val density = new TextField {border = thinBlackBorder}
  val gramsPerPiece = new TextField {border = thinBlackBorder}
  
  val foodView = new GridPanel(8,2) {
    contents += new Label("Food: ")
    contents += foodName
    contents += new Label("Quantity: ")
    contents += foodQuantity
    contents += new Label("Allergen: ")
    contents += allergen
    contents += new Label("Density [g/cm^3 or g/ml]: ")
    contents += density
    contents += new Label("Grams per piece [grams]: ")
    contents += gramsPerPiece
    val newFood = new Button("Save as new food")
    newFood.border_=(greenBorder)
    contents += newFood
    val edit = new Button("Save changes")
    edit.border_=(orangeBorder)
    contents += edit
    val deleteFood = new Button("Delete food")
    deleteFood.border_=(redBorder)
    contents += deleteFood
    val conversion = new Button("Change units")
    conversion.border = cyBorder
    contents += conversion
    val cancel = new Button("Cancel")
    contents += cancel
    
    hGap = 20
    vGap = 15
    border = thinBlackBorder
    this.peer.setPreferredSize(new Dimension(500,500))
    this.peer.setMaximumSize(new Dimension(500, 500))
  }
  
  val foodViewBackground = new BorderPanel(){
    layout += foodView -> BorderPanel.Position.Center
    background = java.awt.Color.lightGray
    border = Swing.EmptyBorder(20, 20, 10, 20)
  }
  
  /* The method 'emptyFoodView' cleans all text fields 
   * in 'foodView'. It is mostly used by the button 
   * 'cancel'.
   */
  private def emptyFoodView() = {
    foodName.text = ""
    foodQuantity.text = ""
    allergen.text = ""
    density.text = ""
    gramsPerPiece.text = ""
  }
  
  
  /* The method 'buildRawView' fills the text fields
   * with the detail of the food selected from 
   * the list on the left part of the GUI.
   */
  private def buildRawView(listview: ListView[String], listIndex: Int) = {
    if (!listview.listData.isEmpty && listIndex < listview.listData.size) {
      val s = listview.listData(listIndex)
      val food = cookbook.storage.allRaw.find(_.name == s)
      if (food.isDefined){
        foodName.text = food.get.name
        foodQuantity.text = food.get.quantity.number.toString() + " " + food.get.quantity.unit
        allergen.text = food.get.printAllergen
        density.text = food.get.returnDensity.toString() 
        gramsPerPiece.text = food.get.returnGramsPerPiece.getOrElse("N/D").toString()
      }
    }
  }
  
  
  /* The method 'buildCookedView' fills the text fields
   * with the detail of the food selected from 
   * the list on the left part of the GUI.
   */
  private def buildCookedView(listview: ListView[String], listIndex: Int) = {
    if (!listview.listData.isEmpty && listIndex < listview.listData.size) {
      val s = listview.listData(listIndex)
      val food = cookbook.storage.allCooked.find(_.name == s)
      if (food.isDefined){
        foodName.text = food.get.name
        foodQuantity.text = food.get.quantity.number.toString() + " " + food.get.quantity.unit
        allergen.text = "\n" + food.get.printAllergen
        density.text = food.get.returnDensity.toString() 
        gramsPerPiece.text = food.get.returnGramsPerPiece.getOrElse("N/D").toString()
      }
    }
  }
  
  /* The method 'buildFoodView' fills the text fields
   * with the detail of the food selected from 
   * the list on the left part of the GUI.
   */
  private def buildFoodView(listview: ListView[String], listIndex: Int) = {
    if (!listview.listData.isEmpty && listIndex < listview.listData.size) {
      val s = listview.listData(listIndex)
      val food = cookbook.storage.allFood.find(_.name == s)
      if (food.isDefined){
        foodName.text = food.get.name
        foodQuantity.text = food.get.quantity.number.toString() + " " + food.get.quantity.unit
        allergen.text = "\n" + food.get.printAllergen
        density.text = food.get.returnDensity.toString() 
        gramsPerPiece.text = food.get.returnGramsPerPiece.getOrElse("N/D").toString()
      }
    }
  }
  
  
  /* RECIPE VIEW
   * This part displays details of recipes in 'newPanel':
   * - 'recipeName': name of the recipe
   * - 'recipeIngredients': name, quantity and unit of the ingredients
   * - 'recipeMissingIng': all the ingredients needed for this recipe that are not in storage
   * - 'recipeAllergens': all the known allegerns of all the ingredients
   * - 'recipeInstructions': text with instructions or comments on the recipe
   * Details are displayed in textFields, where the user can also type.
   * However, the fields corresponding to 'recipeMissingIng' and 'recipeAllergens'
   * are shown with a different background color and can not be edited by the 
   * user, since they are automatically updated every time 'recipeView' is called.
   * Buttons allow the following operations:
   * - 'newRecipe': save the information written in textFields as a new instance of Recipe, 
   * 							if the name is different from the already existing recipes.
   * - 'editRecipe': edit the already existing recipe using the current text in textFields,
   * 							if the name corresponds to an already existing recipe. 
   * - 'deleteRecipe': deletes the recipe from the cookbook
   * - 'cancel': cleans all text fields, but has no effect on the data
   * - 'cook': cooks the recipe, assuming that it can be cooked, by creating a new instance 
   * 					of Cooked food with the same name of the recipe. The quantity is calculated 
   * 					from the cooked quantities of the ingredients. --> see class Cookbook, 'cook'. 
   */
  val recipeName = new TextField {border = thinBlackBorder}
  val recipeIngredients = new TextArea {border = thinBlackBorder}
  val recipeMissingIng = new TextArea {border = thinBlackBorder; editable = false; background = new java.awt.Color(235, 235, 235)}
  val recipeAllergens = new TextArea {border = thinBlackBorder; editable = false; background = new java.awt.Color(235, 235, 235)}
  val recipeInstructions = new TextArea {lineWrap = true; wordWrap = true; border = thinBlackBorder}
  
  val recipeView = new GridPanel(8,2) {
    contents += new Label("Name: ")
    contents += recipeName
    contents += new Label("Ingredients:")
    contents += new ScrollPane(recipeIngredients)
    contents += new Label("Missing ingredients for this recipe:")
    contents += new ScrollPane(recipeMissingIng)
    contents += new Label("Allergens:")
    contents += recipeAllergens
    contents += new Label("Instructions:")
    contents += new ScrollPane(recipeInstructions)
    val newRecipe = new Button("Save as new recipe")
    newRecipe.border = greenBorder
    contents += newRecipe
    val editRecipe = new Button("Save changes")
    editRecipe.border = orangeBorder
    contents += editRecipe
    val deleteRecipe = new Button("Delete recipe")
    deleteRecipe.border_=(redBorder)
    contents += deleteRecipe
    val cook = new Button("Cook")
    cook.border = cyBorder
    contents += cook
    val cancel = new Button("Cancel")
    contents += cancel

    hGap = 20
    vGap = 15
    border = thinBlackBorder
    this.peer.setPreferredSize(new Dimension(500,500))
    this.peer.setMaximumSize(new Dimension(500, 500))
  }
  
  val recipeViewBackground = new BorderPanel(){
    layout += recipeView -> BorderPanel.Position.Center
    background = java.awt.Color.lightGray
    border = Swing.EmptyBorder(20, 20, 10, 20)
  }
  
  /* The method 'emptyRecipeView' cleans all text fields 
   * in 'recipeView'. It is mostly used by the button 
   * 'cancel'.
   */
  private def emptyRecipeView() = {
    recipeName.text = ""
    recipeIngredients.text = ""   
    recipeMissingIng.text = ""
    recipeAllergens.text = ""   
    recipeInstructions.text = ""   
  }
  
  
  /* The method 'buildRecipeView' fills the text fields
   * with the detail of the recipe selected from 
   * the list on the left part of the GUI.
   */
  private def buildRecipeView(listview: ListView[String], listIndex: Int) = {
    if (!listview.listData.isEmpty && listIndex < listview.listData.size) {
      val recipe = listview.listData(listIndex)
      if(cookbook.containsRecipe(recipe)){
        recipeName.text = cookbook.getRecipe(recipe).get.name
        recipeIngredients.text = cookbook.getRecipe(recipe).get.printIngredients
        recipeMissingIng.text = cookbook.getRecipe(recipe).get.missingIngredients.map(_.name).mkString("\n")
        recipeAllergens.text = cookbook.getRecipe(recipe).get.printAllergens
        recipeInstructions.text = cookbook.getRecipe(recipe).get.printDescription    
      }
    }
  }
  
  
  /* SEARCH VIEW
   * This part displays in 'newPanel' results from user defined searches
   *
   * Lists for SEARCH VIEW:
   * They show the output of the user defined search.
   * - foodList: All foods that contain at least one of the strings given as input as substrings in their names 
   * - recNameList: All recipes that contain at least one of the strings given as input as substrings in their names 
   * - recNameCookList: Recipes in recNameList that can be cooked
   * - recIngList: All recipes that contain all strings given as input in their ingredients
   * - recIngCookList: Recipes in recIngList that can be cooked
   * - recQuaList: All recipes that contain exactly the quantity given as input, with respect to the first string given as input
   * - recQuaList: Recipes in recQuaList that can be cooked 
   */
    var foodList = new ListView(Seq[String]())
        foodList.selectionBackground_=(java.awt.Color.WHITE)
    var recNameList = new ListView(Seq[String]())
        recNameList.selectionBackground_=(java.awt.Color.WHITE)
    var recNameCookList = new ListView(Seq[String]())
        recNameCookList.selectionBackground_=(java.awt.Color.WHITE)
    var recIngList = new ListView(Seq[String]())
        recIngList.selectionBackground_=(java.awt.Color.WHITE)
    var recIngCookList = new ListView(Seq[String]())
        recIngCookList.selectionBackground_=(java.awt.Color.WHITE)
    var recQuaList = new ListView(Seq[String]())
        recQuaList.selectionBackground_=(java.awt.Color.WHITE)
    var recQuaCookList = new ListView(Seq[String]())
        recQuaCookList.selectionBackground_=(java.awt.Color.WHITE)
  
  
  val searchView = new GridPanel(7,3) {
    contents += new Label("Foods whose name contains your keyword(s):")
    contents += new ScrollPane(foodList)
    contents += new Label("Recipes whose name contains your keyword(s): ")
    contents += new ScrollPane(recNameList)
    contents += new Label("From recipes above, you can cook: ")
    contents += new ScrollPane(recNameCookList) 
    contents += new Label("Recipes containing all your keywords as ingredients:")
    contents += new ScrollPane(recIngList)
    contents += new Label("From recipes above, you can cook: ")
    contents += new ScrollPane(recIngCookList)
    contents += new Label("The specific quantity you gave as input can be used to cook:")
    contents += new ScrollPane(recQuaList)
    contents += new Label("From the list above, you can cook:")
    contents += new ScrollPane(recQuaCookList)
    
    hGap = 5
    vGap = 15
    border = thinBlackBorder
    preferredSize = new Dimension(500,500)
    maximumSize = new Dimension(500, 500)
  }
  
  val searchViewBackground = new BorderPanel{
    layout += searchView -> BorderPanel.Position.Center
    background = java.awt.Color.lightGray
    border = Swing.EmptyBorder(20, 20, 10, 20)
  }

  
  
  /* Listen to events in the GUI */
  listenTo(newPanel)
  listenTo(saveButton)
  listenTo(userDef.box.checkE)
  listenTo(userDef.box.checkG)
  listenTo(userDef.box.checkL)
  listenTo(userDef.box.checkN)
  listenTo(userDef.box.checkF)
  listenTo(userDef.findButton)
  listenTo(foodView.edit)
  listenTo(foodView.newFood)
  listenTo(foodView.cancel)
  listenTo(foodView.deleteFood)
  listenTo(foodView.conversion)
  listenTo(recipeView.editRecipe)
  listenTo(recipeView.newRecipe)
  listenTo(recipeView.cancel)
  listenTo(recipeView.deleteRecipe)
  listenTo(recipeView.cook)
  listenTo(cups)
  listenTo(kg)
  listenTo(grams)
  listenTo(liters)
  listenTo(dl)
  listenTo(pieces)
  listenTo(dropdown.selection)
  listenTo(list.selection)
  listenTo(foodList.selection)
  listenTo(recNameList.selection)
  listenTo(recNameCookList.selection)
  listenTo(recIngList.selection)
  listenTo(recIngCookList.selection)
  listenTo(recQuaList.selection)
  listenTo(recQuaCookList.selection)
  
  
  /* 'buildRawList', 'buildCookedList', 'buildRecipeList', 
   * 'buildCanCookList', 'buildMissingList', buildAllList' 
   * update the list in the left part of the GUI.
   * - 'buildRawList' collects all raw foods.
   */
  
  private def buildRawList = {
    var myList = cookbook.storage.allRaw
    if (userDef.box.checkE.selected == true){
      myList = cookbook.storage.rawWithout(myList, "E")
    }
    if (userDef.box.checkG.selected == true){
      myList = cookbook.storage.rawWithout(myList, "G")
    }
    if (userDef.box.checkL.selected == true){
      myList = cookbook.storage.rawWithout(myList, "L")
    }
    if (userDef.box.checkN.selected == true){
      myList = cookbook.storage.rawWithout(myList, "N")
    }
    if (userDef.box.checkF.selected == true){
      myList = cookbook.storage.rawWithout(myList, "F")
    }
    list.listData = myList.toSeq.map(_.name).sorted
    list.listData
  }
  
  
  /* - 'buildCookedList' collects all cooked foods. */
  private def buildCookedList = {
    var myList = cookbook.storage.allCooked
    if (userDef.box.checkE.selected == true){
      myList = cookbook.storage.cookedWithout(myList, "E")
    }
    if (userDef.box.checkG.selected == true){
      myList = cookbook.storage.cookedWithout(myList, "G")
    }
    if (userDef.box.checkL.selected == true){
      myList = cookbook.storage.cookedWithout(myList, "L")
    }
    if (userDef.box.checkN.selected == true){
      myList = cookbook.storage.cookedWithout(myList, "N")
    }
    if (userDef.box.checkF.selected == true){
      myList = cookbook.storage.cookedWithout(myList, "F")
    }
    list.listData = myList.toSeq.map(_.name).sorted
    list.listData
  }

  
  /* - 'buildRecipeList' collects all recipes. */
  private def buildRecipeList = {
    var myList = cookbook.allRecipes
    if (userDef.box.checkE.selected == true){
      myList = cookbook.recipeWithout(myList, "E")
    }
    if (userDef.box.checkG.selected == true){
      myList = cookbook.recipeWithout(myList, "G")
    }
    if (userDef.box.checkL.selected == true){
      myList = cookbook.recipeWithout(myList, "L")
    }
    if (userDef.box.checkN.selected == true){
      myList = cookbook.recipeWithout(myList, "N")
    }
    if (userDef.box.checkF.selected == true){
      myList = cookbook.recipeWithout(myList, "F")
    }
    list.listData = myList.toSeq.map(_.name).sorted
    list.listData
  }
  
  
  /* - 'buildCanCookList' collects all recipes that can be cooked. */  
  private def buildCanCookList = {
    var myList = cookbook.canCook
    if (userDef.box.checkE.selected == true){
      myList = cookbook.recipeWithout(myList, "E")
    }
    if (userDef.box.checkG.selected == true){
      myList = cookbook.recipeWithout(myList, "G")
    }
    if (userDef.box.checkL.selected == true){
      myList = cookbook.recipeWithout(myList, "L")
    }
    if (userDef.box.checkN.selected == true){
      myList = cookbook.recipeWithout(myList, "N")
    }
    if (userDef.box.checkF.selected == true){
      myList = cookbook.recipeWithout(myList, "F")
    }
    list.listData = myList.toSeq.map(_.name).sorted
    list.listData
  }
  
  
  /* - 'buildMissingList' collects all recipes that 
   * need n more ingredients to be cooked.
   */   
  private def buildMissingList(n: Int) = {
    var myList = cookbook.nMoreNeeded(n)
    if (userDef.box.checkE.selected == true){
      myList = cookbook.recipeWithout(myList, "E")
    }
    if (userDef.box.checkG.selected == true){
      myList = cookbook.recipeWithout(myList, "G")
    }
    if (userDef.box.checkL.selected == true){
      myList = cookbook.recipeWithout(myList, "L")
    }
    if (userDef.box.checkN.selected == true){
      myList = cookbook.recipeWithout(myList, "N")
    }
    if (userDef.box.checkF.selected == true){
      myList = cookbook.recipeWithout(myList, "F")
    }
    list.listData = myList.toSeq.map(_.name).sorted
    list.listData
  }

  
  /* - 'buildAllList' collects all known items,
   * both foods and recipes.
   */   
  private def buildAllList = {
    list.listData = (buildRawList ++ buildCookedList ++ buildRecipeList).sorted
    list.listData
  }
  
  
  /* 'resetLHS' collects all known items 
   * and reset the list under the dropdown menu, 
   * setting it to '- Or select...' status.
   */ 
  private def resetLHS() = {
    buildAllList
    list.repaint()
    newPanel.repaint()
    dropdown.selection.index = 0
  }
  
  /************************************************************************************************/
  /*																																															*/
  /* REACTIONS START HERE																																					*/
  /*																																															*/
  /************************************************************************************************/
  
  /* Reactions to 'dropdown' selection and selection from 'list'
   * (the left part of the GUI)
   */
  reactions += {
      case a: scala.swing.event.SelectionChanged =>      
        {
          /* dropdown selection updates the list below the dropdown menu 
           * depending on the selected category:
           * 1 - raw food
           * 2 - cooked food
           * 3 - all recipes
           * 4 - recipes that can be cooked
           * 5 - 1 more ingredient needed
           * 6 - 2 more ingredients needed
           * 7 - 3 more ingredients needed
           */
          if(dropdown.selection.index == 1 && a.source == dropdown) {
              buildRawList 
              newPanel.repaint()
              newPanel.revalidate()
          } else if(dropdown.selection.index == 2 && a.source == dropdown){
              buildCookedList
              newPanel.repaint()
              newPanel.revalidate()
          } else if(dropdown.selection.index == 3 && a.source == dropdown){
              buildRecipeList
              newPanel.repaint()
              newPanel.revalidate()
          } else if(dropdown.selection.index == 4 && a.source == dropdown){
              buildCanCookList
              newPanel.repaint()   
              newPanel.revalidate()
          } else if(dropdown.selection.index == 5 && a.source == dropdown){
              buildMissingList(1)
              newPanel.repaint()   
              newPanel.revalidate()
          } else if(dropdown.selection.index == 6 && a.source == dropdown){
              buildMissingList(2)
              newPanel.repaint()   
              newPanel.revalidate()
          } else if(dropdown.selection.index == 7 && a.source == dropdown){
              buildMissingList(3)
              newPanel.repaint() 
              newPanel.revalidate()
          
          /* dropdown selection and selecting from the list below it
           * builds the bigger panel with details of the selected item
           */
          } else if (dropdown.selection.index == 1 && a.source == list) {     
              newPanel.layout(foodViewBackground) = BorderPanel.Position.Center
              if (list.selection.leadIndex < list.listData.size) {
                buildRawView(list, list.selection.leadIndex) 
              } 
              foodView.repaint()
              foodView.revalidate()
              foodViewBackground.repaint()
              foodViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (dropdown.selection.index == 2 && a.source == list) {
              newPanel.layout(foodViewBackground) = BorderPanel.Position.Center
              if (list.selection.leadIndex < list.listData.size) {
                buildCookedView(list, list.selection.leadIndex) 
              }    
              foodView.repaint()
              foodView.revalidate()
              foodViewBackground.repaint()
              foodViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (dropdown.selection.index >= 3 && a.source == list) {
              newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              if (list.selection.leadIndex < list.listData.size) {
                buildRecipeView(list, list.selection.leadIndex)
              } 
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          
          /* selecting one item from the lists that are built after a userdefined search
           * shows details of the selected item in popUpWindow. 
           * The code for showing details in the central panel is commented out.
           */
          } else if (a.source == foodList) {                              
              //newPanel.layout(foodViewBackground) = BorderPanel.Position.Center 
              buildFoodView(foodList, foodList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, foodViewBackground) 
              // <- stop here
              foodView.repaint() 
              foodView.revalidate()
              foodViewBackground.repaint()
              foodViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recNameList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recNameList, recNameList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recNameCookList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recNameCookList, recNameCookList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recIngList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recIngList, recIngList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recIngCookList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recIngCookList, recIngCookList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recQuaList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recQuaList, recQuaList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else if (a.source == recQuaCookList) {
              //newPanel.layout(recipeViewBackground) = BorderPanel.Position.Center
              buildRecipeView(recQuaCookList, recQuaCookList.selection.leadIndex)
              //comment this part and uncomment newPanel.layout to show details in the same window ->
              window = popWindow(window, recipeViewBackground) 
              // <- stop here
              recipeView.repaint()
              recipeView.revalidate()
              recipeViewBackground.repaint()
              recipeViewBackground.revalidate()
              newPanel.repaint() 
              newPanel.revalidate()
          } else { 
              resetLHS()
          }
        }
  }
  
  
  /* Reactions to user-defined searches:
   * 		'textSearch', 'quantSearchField', checkboxes, 
   * 		radio buttons for units,	and 'findButton'
   * Reactions to texteditor mode, saveFile
   * Reactions to buttons in foodView and in recipeView
   */ 
  reactions += {
      case b: scala.swing.event.ButtonClicked => {
        window.visible = false
        
        /* saveButton from text-editor mode saves the file */
        if (b.source == saveButton) {
          saveFile()
        }
                
        /**********************************************************************/
        /* 	USER DEFINED SEARCH STARTS HERE																	  */
        /* 																																		*/ 
        /**********************************************************************/
        
        /* userDef.findButton collects data from the first 2 line of the GUI 
         * and performs the search, based on the inputs
         */
        else if (b.source == userDef.findButton){             
          
          // Reset the panel and clean the list
          resetLHS()
          emptyRecipeView()
          emptyFoodView()              
          foodList.listData = Buffer[String]()
          recNameList.listData = Buffer[String]()
          recNameCookList.listData = Buffer[String]()
          recIngList.listData = Buffer[String]()
          recIngCookList.listData = Buffer[String]()
          recQuaList.listData = Buffer[String]()
          recQuaCookList.listData = Buffer[String]()
          
          //Search by ingredients
          val keyword = userDef.textSearch.text.trim().toLowerCase()
          var keysArray = Array(keyword)
          if (keyword.contains(",")){
            keysArray = keyword.split(",")
            keysArray
          }
          if (keyword.isEmpty() || keysArray.isEmpty) {
            newPanel.layout(searchViewBackground) = BorderPanel.Position.Center
            newPanel.repaint()
            popUp("This was an empty search.")
            window.visible_=(false)
          }
          
          var searchFoo = cookbook.storage.findPartial(keysArray)
          var searchRec = cookbook.findPartialName(keysArray)
          var searchRecW = cookbook.recipesContainingAllIng(keysArray)
          var searchQua = Buffer[Recipe]()
          val temp = cookbook.recipesContainingAllIng(Array(keysArray.head))
          
          //Search by quantity: the quantity is related to the first ingredient
          val quantInput = userDef.quantSearchField.text.trim().toLowerCase()
          var unitInput = ""
          if (!quantInput.isEmpty() && Try{quantInput.toDouble}.isSuccess && quantInput.toDouble >= 0.0){
            if (grams.selected) {
              unitInput = "grams"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else if (kg.selected) {
              unitInput = "kg"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else if (liters.selected) {
              unitInput = "liters"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else if (dl.selected) {
              unitInput = "dl"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else if (cups.selected) {
              unitInput = "cups"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else if (pieces.selected) {
              unitInput = "pieces"
              val quantToMatch = new Measure(quantInput.toDouble, unitInput)
              searchQua = cookbook.recipeWithFoodAndQuantity(temp, keysArray(0), quantToMatch)
            }
            else {
              popUp("Select one unit.")
            }
          }
          else if (!quantInput.isEmpty()) 
            popUp("Write a number without any letter in the smaller text field (eg. 300).")

          
          newPanel.layout(searchViewBackground) = BorderPanel.Position.Center          
          
          if (userDef.box.checkE.selected == true){
              searchFoo = cookbook.storage.foodWithout(searchFoo, "E")
              searchRec = cookbook.recipeWithout(searchRec, "E")              
              searchRecW = cookbook.recipeWithout(searchRecW, "E")
              searchQua = cookbook.recipeWithout(searchQua, "E")
            }            
          if (userDef.box.checkG.selected == true){              
              searchFoo = cookbook.storage.foodWithout(searchFoo, "G")
              searchRec = cookbook.recipeWithout(searchRec, "G")
              searchRecW = cookbook.recipeWithout(searchRecW, "G")
              searchQua = cookbook.recipeWithout(searchQua, "G")
            }             
          if (userDef.box.checkL.selected == true){
              searchFoo = cookbook.storage.foodWithout(searchFoo, "L")
              searchRec = cookbook.recipeWithout(searchRec, "L")
              searchRecW = cookbook.recipeWithout(searchRecW, "L")
              searchQua = cookbook.recipeWithout(searchQua, "L")
            } 
          if (userDef.box.checkN.selected == true){
              searchFoo = cookbook.storage.foodWithout(searchFoo, "N")  
              searchRec = cookbook.recipeWithout(searchRec, "N")
              searchRecW = cookbook.recipeWithout(searchRecW, "N")
              searchQua = cookbook.recipeWithout(searchQua, "N")
            } 
          if (userDef.box.checkF.selected == true){
              searchFoo = cookbook.storage.foodWithout(searchFoo, "F")  
              searchRec = cookbook.recipeWithout(searchRec, "F")
              searchRecW = cookbook.recipeWithout(searchRecW, "F")
              searchQua = cookbook.recipeWithout(searchQua, "F")
            }           
          
            foodList.listData = searchFoo.map(_.name).sorted
            recNameList.listData = searchRec.map(_.name).sorted
            recNameCookList.listData = searchRec.filter(p => cookbook.recursiveCanBeCooked(p)).map(_.name).sorted
            recIngList.listData = searchRecW.map(_.name).sorted
            recIngCookList.listData = searchRecW.filter(p => cookbook.recursiveCanBeCooked(p)).map(_.name).sorted
            recQuaList.listData = searchQua.map(_.name).sorted
            recQuaCookList.listData = searchQua.filter(p => cookbook.recursiveCanBeCooked(p)).map(_.name).sorted
            
            foodList.repaint()
            recNameList.repaint() 
            recNameCookList.repaint()
            recIngList.repaint()
            recIngCookList.repaint()
            recQuaList.repaint()
            recQuaCookList.repaint()
            newPanel.repaint()
            
//          Uncomment next 3 lines to clean both search fields after pressing 'Find'            
//          userDef.quantSearchField.text = ""
//          userDef.textSearch.text = ""   
        }

        /**********************************************************************/
        /* 	USER DEFINED SEARCH ENDS HERE																	    */
        /* 																																		*/
        /* 	INTERACTION WITH FOOD STARTS HERE																	*/ 
        /**********************************************************************/
                                                                              
        /* 'foodView.cancel' cleans the text fields. It doesn't have any effect on data. */
        if (b.source == foodView.cancel){                                     
          emptyFoodView()                                                     
        }                                                                     
        
        /* 'foodView.cancel' deletes the food from the cookbook and cleans the tex fields after. */
        if (b.source == foodView.deleteFood){                                 
          val possibleFood = cookbook.storage.findFood(foodName.text.trim())  
          if (possibleFood.isDefined) {                                       
            if(cookbook.storage.deleteFood(possibleFood.get)){
              popUp("You have deleted this item.")
              emptyFoodView()
            }
          }
        }
        
        /* 'foodView.edit' reads the text fields and saves the informations in the same food item, if possible */
        if (b.source == foodView.edit) {
          
          //Read
          val name = foodName.text.trim().toLowerCase()
          val qField = foodQuantity.text.trim().split(" ")
          val all = allergen.text.trim().toUpperCase()
          val dens = density.text.trim()
          val grPcs = gramsPerPiece.text.trim()
          
          var readGrPcs = 0.0
          
          //Check consistency and save
          if (Try{grPcs.toDouble}.isSuccess && grPcs.toDouble >= 0.0) {
            readGrPcs = (grPcs.toDouble)
          } else if (grPcs.trim() != "N/D"){
            popUp("Grams per piece should be a non-negative number.")
          }
          if (name.isEmpty() || qField.length < 2 || dens.isEmpty()){
            popUp("The only fields that can be empty are \"Allergen\" and \"Grams per piece\".\nIf you don't know the density, write 1.")
          }
          else if (dens.split(' ').size>1 || !Try{dens.toDouble}.isSuccess || dens.toDouble < 0.0) {
            popUp("Write a non-negative number without units in \"Density\" (eg. 1).")
          }
          else if(cookbook.storage.findFood(name).isDefined){
            val food = cookbook.storage.findFood(name).get
            if (Try{qField(0).trim().toDouble}.isSuccess && qField(0).trim().toDouble>=0.0){
              val newQuantity = new Measure(qField(0).trim().toDouble, qField(1).trim().toLowerCase())
              food.quantity = newQuantity
              food.deleteAllergen()
              food.addAllergen(all)
              food.setDensity(dens.toDouble)
              food.setGramsPerPiece(readGrPcs)
            } else {
              popUp("Quantity seems wrong. Write a non-negative number and a unit, with a space in between.")
            }
          } else {
            popUp("Is this a new food? You can't change the name.")
          }
            
        }
        /* 'foodView.newFood' reads the text fields and saves the informations in a new food item, if possible */
        if (b.source == foodView.newFood) {      
          
          //Read
          val name = foodName.text.trim().toLowerCase()
          val qField = foodQuantity.text.trim().split(" ")
          val all = allergen.text.trim().toUpperCase()
          val dens = density.text.trim()
          val grPcs = gramsPerPiece.text.trim()
          
          var readGrPcs = 0.0
          
          //Check consistency and save
          if (Try{grPcs.toDouble}.isSuccess) {
            readGrPcs = (grPcs.toDouble)
          } else if (grPcs.trim() != "N/D") {
            popUp("Grams per piece should be a non-negative number.")
          }
          if (name.isEmpty() || qField.length < 2 || dens.isEmpty()){
            popUp("The only fields that can be empty are \"Allergen\" and \"Grams per piece\".\nIf you don't know the density, write 1.")
          }
          else if (dens.split(' ').size>1 || !Try{dens.toDouble}.isSuccess || dens.toDouble < 0.0) {
            popUp("Write a non-negative number without units in \"Density\" (eg. 1).")
          }
          else if(!cookbook.storage.findFood(name).isDefined && !name.isEmpty()){
            if (Try{qField(0).trim().toDouble}.isSuccess && qField(0).trim().toDouble >= 0.0){
              val newQuantity = new Measure(qField(0).trim().toDouble, qField(1).trim().toLowerCase())
              popUpFood() match {
                case "Raw" => {
                  val newFood = new Raw(name, newQuantity)
                  newFood.addAllergen(all)
                  newFood.setDensity(dens.toDouble)
                  newFood.setGramsPerPiece(readGrPcs)
                  cookbook.storage.addRaw(newFood)
                }
                case "Cooked" => {
                  val newFood = new Cooked(name, newQuantity)
                  newFood.addAllergen(all)
                  newFood.setDensity(dens.toDouble)
                  newFood.setGramsPerPiece(readGrPcs)
                  cookbook.storage.addCooked(newFood)
                }
                case _ => {
                  // nothing should happen
                }
              }
              resetLHS()
            } else {
              popUp("Quantity seems wrong. Write a non-negative number and a unit, with a space in between.")
            }
          } else {
            popUp("Use another name for this new food.")
          }
        }
        
        /* 'foodView.conversion' asks the user for a new unit via popUpConvert()
         * and performs the conversion, if possible, by saving the answer and 
         * printing it in the text field showing the quantity.
         */
        if (b.source == foodView.conversion) {                       
          var currentU = ""
          
          //Read
          val name = foodName.text.trim().toLowerCase()
          val qField = foodQuantity.text.trim().split(" ")
          val dens = density.text.trim()
          val grPcs = gramsPerPiece.text.trim()
          
          var readGrPcs : Option[Double] = None
          
          //Check consistency and perform unit conversion
          if (Try{grPcs.toDouble}.isSuccess && grPcs.toDouble >=0.0) {
            readGrPcs = Some(grPcs.toDouble)
          }
          
          if (qField.length < 2 || !Try{qField(0).trim().toDouble}.isSuccess || qField(0).trim().toDouble < 0.0){
            popUp("Quantity needs a non-negative number and a unit.")
          } else { 
            currentU = qField(1).trim().toLowerCase()
          }

          if (name.isEmpty() || dens.isEmpty()){
            popUp("The only field that can be empty is \"Allergen\".\nIf you don't know the density, write 1.")
          }
          else if (dens.split(' ').size>1 || !Try{dens.toDouble}.isSuccess || dens.toDouble < 0.0) {
            popUp("Write a non-negative number without units in \"Density\" (eg. 1).")
          }
          else {
            val target = popUpConvert()
            if (!target.isEmpty() && !currentU.isEmpty() && 
                target != currentU && cookbook.storage.findFood(name).isDefined){
              val food = cookbook.storage.findFood(name).get
              val oldQuantity = new Measure(qField(0).trim().toDouble, currentU)
              val newQuantity = oldQuantity.convert(target, dens.toDouble, readGrPcs)
              food.quantity = newQuantity
              foodQuantity.text = food.quantity.number.toString() + " " + food.quantity.unit
              newPanel.repaint()
            }
          } 
        }                                  
                                                                                
        /**********************************************************************/
        /* INTERACTION WITH FOOD ENDS HERE																	  */
        /* 																																		*/ 
        /* INTERACTION WITH RECIPE STARTS HERE																*/
        /*																																		*/
        /**********************************************************************/
                                                                                
        /* 'recipeView.cancel' cleans the text fields. It has no impact on app data. */
        if (b.source == recipeView.cancel){                                     
          emptyRecipeView()                                                     
        }                                                           
        
        /* 'recipeView.deleteRecipe' permanently deletes the recipe and 
         * cleans the text fields afterwards. 
         */
        if (b.source == recipeView.deleteRecipe){                               
          val possibleRec = recipeName.text.trim().toLowerCase()                
          if (cookbook.containsRecipe(possibleRec)) {                           
            cookbook.deleteRecipe(possibleRec)
            popUp("You have deleted this item.")
            emptyRecipeView()
          }
        }
        
        /* 'recipeView.editRecipe' reads the text fields and saves 
         * the informations in the already existing recipe, if possible.
         */
        if (b.source == recipeView.editRecipe){                                  
          val name = recipeName.text.trim().toLowerCase()
          val ing = recipeIngredients.text.trim()
          val ins = recipeInstructions.text.trim()
          if (name.isEmpty() || ing.isEmpty || ins.isEmpty()){
            popUp("Fill everything, except \"Allergens\" and \"Missing ingredients\". " +
                  "Write 'ingredient: number unit', then new line and next ingredient.")          
          }
          else if (cookbook.getRecipe(name).isDefined){
            val oldRecipe = cookbook.getRecipe(name).get
            var array = ing.split('\n')
            if (array.isEmpty) {
              popUp("Write 'ingredient: number unit', then new line and next ingredient.")
              }
            if (!array.isEmpty){
              oldRecipe.deleteAllIngredients()
              for (i <- array){
                readIngredients(oldRecipe, i)
              }            
              oldRecipe.addDescription(ins)
            }
          } else {
            popUp("Is this a new recipe? You can't change the name.")
          }
        }
        
        /* 'recipeView.newRecipe' reads the text fields and adds a new recipe, if possible */
        if (b.source == recipeView.newRecipe){                                    
          val name = recipeName.text.trim().toLowerCase()
          val ing = recipeIngredients.text.trim()
          val ins = recipeInstructions.text.trim()
          if (name.isEmpty() || ing.isEmpty || ins.isEmpty()){
            popUp("Fill everything, except \"Allergens\" and \"Missing ingredients\". " +
                  "Write 'ingredient: number unit', then new line and next ingredient.")
          }
          else if (!cookbook.getRecipe(name).isDefined){
            val newRecipe = new Recipe(name)
            var array = ing.split('\n')
            if (array.isEmpty) {
              popUp("Write 'ingredient: number unit', then new line and next ingredient.")
            }
            if (!array.isEmpty){
              for (i <- array){
                readIngredients(newRecipe, i)
              }            
            newRecipe.addDescription(ins)
            cookbook.addRecipe(newRecipe)
            resetLHS()
            }
          } else {
            popUp("Use another name for this new recipe.")
          }
        }
        
        /* 'recipeView.cook' cooks the recipe, if possible. */
        if (b.source == recipeView.cook){
          val name = recipeName.text.trim().toLowerCase()
          if (cookbook.getRecipe(name).isDefined) {
            val recipe = cookbook.getRecipe(name).get
            if (!cookbook.cook(recipe)) popUp("I can't cook it.")
            else popUp("I cooked it!")
            resetLHS()
            }
        }

      }
    }

  /**************************************************************************/
  /* END OF REACTIONS																												*/
  /* 																											                  */
  /* HERE BELOW ALL POPUP WINDOWS.																					*/				
  /**************************************************************************/
  
  /* New window that appears in case of errors or to give advice to the user.
   * It takes the message as parameter. 
   */
  private def popUp(message: String) = {
    Dialog.showMessage(top, message)
  }
  
  
  /* New window that opens up from 'Menu' -> 'How this works'.
   * It explains how to start using the Cookbook and which are
   * the main actions users can do. It doesn't have any button,
   * except for the default X. By pressing that, the popup is closed 
   * and the user can continue from where the cookbook was before the popup. 
   */
  private def popUpInfos() = {
    val infos = new Frame(){
      title = "How to use it"
      val guide = new TextArea("\n Import your data by clicking on 'Menu' and 'Import data', then select your data file. " +
                               "\n\n Foods and recipes will be listed on the left. By using the dropdown menu on the left, " +
                               "you select the category. When the dropdown shows '- Or select from here to list all', "+
                               "items in the list below cannot be selected. In order to select one item, the category " +
                               "must be selected first. " +
                               "Once one category is selected, click on the name of an item " +
                               "to see detailed information in the central panel. " +
                               "The dropdown menu lists also what can be cooked without going to the supermarket, " +
                               "or with 1 to 3 additional ingredients.\n" + 
                               "If you want to filter out allergens, tick the allergens you want to avoid, "+
                               "and then click on the dropdown menu to select the category, and then select one item. "+
                               "\n\n The top part of the GUI allows for user-defined searches. " +
                               "Type the name of an ingredient or a recipe in the bigger text field: "+
                               "for example, type 'c', or 'cookies', and then press the button 'Find'. " +  
                               "You can search more items at the same time by typing comma ',' between words: " +
                               "for example, type 'a, b, c', and then press the button 'Find'. "+
                               "You can look for recipes containing a specific quantity of an ingredient by " +
                               "writing the ingredient in the bigger text field, and writing the quantity in " +
                               "the smaller text field. Write the number only (eg. 300), and don't write the unit: " +
                               "instead, select the unit amongs the options provided. " +
                               " For example, write 'eggs' in the bigger text field, " +
                               " write '2' in the smaller text field in the second row, select 'pieces', "+
                               "and then press the button 'Find'."+
                               "\n\n If you tick allergens before pressing 'Find', "+
                               "items containing the thicked allergen(s) will not be showed in the search results." +
                               "\n\n After clicking on the left dropdown menu and then on a food item, "+
                               "the detailed view that appears in the central panel allows to " +
                               "add new foods, modify existing foods, delete foods, and perform unit conversions. " +
                               "\n After clicking on the left dropdown menu and then on a recipe item, "+
                               "the detailed view that appears in the central panel allows to " +
                               "add a new recipe, modify existing recipes, delete recipes, and cook recipes. " +
                               "\n After performing a user-defined search, a popup window will show details "+
                               "of the selected item from the search results. " +
	                             "\n\n The data of the cookbook can be exported by clicking on 'Menu' and 'Export data'." +
	                             "Data will be saved in a compatible format, so that they can be loaded again. "+
	                             "\n\n Data file can be manually inspected and edited via 'Menu' and 'Open File'. "+
                               "\n\n Have fun with the cookbook!\n\n"){
        lineWrap = true; wordWrap = true; 
      }
      guide.font_=(new Font("Arial", 0, 14))
      guide.editable = false
      guide.background = new java.awt.Color(254, 255, 221)
      
      contents = new BorderPanel(){
        layout += new ScrollPane(guide) -> BorderPanel.Position.Center
        background = java.awt.Color.lightGray
        border = Swing.EmptyBorder(20, 20, 10, 20)
      }
  
      visible = true
      size = new Dimension(400,400)
      centerOnScreen
   }
    infos
  }
  
  
  /* New window that opens up when clicking the button 'Convert' in foodView.
   * It shows all the supported units. The user can choose one to perform
   * the conversion, or go back. 
   */
  private def popUpConvert() : String ={                      
    val s = Dialog.showInput(top, "Select new units:", "Convert quantity", entries=options, initial=1) 
    s match {
              case Some(u) => return u.toString()
              case None => ""
            }
  }
  
  
  /* All the units for which the cookbook supports unit conversions.*/
  val options = List("pieces", "cups","kg","hg","dag","grams","dg","cg","mg","kl","hl","dal","liters","dl","cl","ml")
 
  
  /* 'popWindow' builds on the frame 'window' the popup
   * window showing the details of one item after
   * user defined search.
   */
  private def popWindow(win: Frame, thisView: Panel) = {
    win.repaint()
    win.validate()
    win.contents = new BoxPanel(Orientation.Vertical){
      contents +=(thisView)
      thisView.border = thinBlackBorder
      background = java.awt.Color.lightGray
      border = Swing.EmptyBorder(10, 20, 10, 20)
     }
    win.size = new Dimension(400, 490)
    win.visible = true
    win
  }
  
  
  /* 'popUpFood' allows to choose whether the new food should be raw or cooked */
  private def popUpFood() : String ={ 
    val rawOrCooked = List("Raw", "Cooked")
    val s = Dialog.showInput(top, "What food do you want to create?\n" + 
                            "Raw food only have 1 allergen, Cooked food can have more.", 
                             "Create new food", entries=rawOrCooked, initial=1) 
    s match {
              case Some(u) => return u.toString()
              case None => ""
            }
  }
  
  /**************************************************************************/
  /* END OF POPUP WINDOWS																										*/
  /* 																											                  */
  /* HERE BELOW HELPER METHOD FOR READING INGREDIENTS FROM RECIPEVIEW				*/				
  /**************************************************************************/
  
  /* Helper function that reads ingredients from recipeView */
  private def readIngredients(oldRecipe: Recipe, i: String) : Boolean = {
    val food = i.trim().split(':')
    if (food.length <2) {
      popUp("Write 'ingredient: number unit', then new line and next ingredient.")
      false
    } else {
      val possibleFood = cookbook.storage.findFood(food(0).trim().toLowerCase())
      val possibleQuan = food(1).trim().split(' ')
      if (possibleQuan.length < 2 || !Try{possibleQuan(0).toDouble}.isSuccess || possibleQuan(0).trim().toDouble<0.0) {
        popUp("Quantity seems wrong.")
      } else if (!options.contains(possibleQuan(1).trim().toLowerCase())) {
        popUp("You need to enter a supported unit. Try 'grams', 'liters', or 'cups'.")
      } else {
        val q = new Measure(possibleQuan(0).toDouble, possibleQuan(1).trim().toLowerCase())
        if (possibleFood.isDefined){
          oldRecipe.saveIngredient(possibleFood.get, q)
        } else {
          if (food(0).trim() != oldRecipe.name){
            val newFood = new Raw(food(0).trim(), new Measure(0.0, possibleQuan(1).trim().toLowerCase()))
            cookbook.storage.addRaw(newFood)
            oldRecipe.saveIngredient(newFood, q)
          } else {
            popUp("Ingredients name must be different from recipe name to avoid infinite loops.")
          }
        }
      }
      true
    }
  }
 
}