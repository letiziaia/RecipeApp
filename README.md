# RecipeApp
Course project

## General Description
The app implements a recipe book that can be searched for food and recipes that meet the
appropriate criteria (e.g. "with chicken", and/or "without lactose").
Users can search for recipes using one or more ingredients, and food may be labeled 
with an allergic factor (lactose, gluten, eggs, nuts), so that the program can restrict 
the search in order to avoid specific allergens. The app reads updated 
information about the content of the food storage (e.g. groceries and
their quantities), and therefore is able to filter for the recipes that can be prepared with the
available ingredients only. After instructing the app to cook some recipe, the quantities of 
food in the storage are updated accordingly.  

## Instructions
After launching the app, instructions can be found under "Menu"->"How this works".
Recipes and different kind of food are stored in the text file called "data". To import the data,
click on "Menu"->"Import Data", and then choose the data file. After the text file has been loaded, 
the user can see a list of items in the left panel of the GUI window, below the dropdown menu. 

### Single search
Type text or numbers in the bigger text field indicated by "Write an ingredient or a recipe", then press "Find".
The app will try to find foods and recipes that contain that input as a substring in their names or ingredients. 

### Multiple search
Write different text strings separated by commas in the bigger text field indicated by "Write an ingredient or a recipe", 
then press "Find".

### Excluding allergens from the search result
Tick checkboxes with allergens that need to be filtered out, and then continue as for a single search or a multiple search.  
Foods and recipes containing the allergens that are selected will not be shown in the search result. 

### Search by quantity
Write the quantity as a number in the smaller text field indicated by "Write quantity". 
Select also the unit from the options on the right of the text field. 
Then, continue as for a single search. 
In case of a multiple search, the quantity will be associated to the first string of the multiple search only. 

### Add a new item
After clicking on a food item, it is possible to add a new food by writing a name that does not correspond 
to any other food already in the storage, then filling the other text fields with details, and finally pressing 
the button "Save as new food". 
In the same way, after clicking on a recipe item, it is possible to add a new recipe by writing a recipe name 
that does not correspond to any other saved recipes, then filling the other text fields with details, and finally 
pressing the button "Save as new recipe".
In order to avoid mistake, it is recommended to use the button "Cancel" to clean all text fields before filling the 
information for a new item. Notice that the button "Cancel" does not modify the data stored in the cookbook.

### Modify an item
Details of a food such as quantity in storage, allergens, etc. can be edited by writing in the text fields and 
then pressing the button "Save changes". 
In the same way, details of a recipe such as ingredients and instructions can be edited by writing in the text fields 
and then pressing the button "Save changes". 
In both cases, when saving changes, the app requires that the name of the item remains the same.

### Delete an item
An item (food or recipe) can be permanently deleted by pressing the button "Delete".

### Export the current data
The data currently stored in the app can be exported in a text file, by selecting "Menu"->"Export Data". 

### Text editor 
The data file can be inspected and also edited by selecting "Menu"->"Open File".
