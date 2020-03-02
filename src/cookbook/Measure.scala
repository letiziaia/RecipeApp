package cookbook

import scala.math._

class Measure (quantity: Double, unitName: String) {
    
    private var thisQuantity = quantity
    private var thisUnit = unitName
  
  //density must be given as g/cm^3 which is equal to g/ml <- this is a specific property of each food
    
    def number = thisQuantity
    def unit = thisUnit
        
    def add(thisMore: Double) = new Measure(number+thisMore, unit)
    def subtract(thisLess: Double) = new Measure(max(0.0, number-thisLess), unit)
    
    def isWeight : Boolean = Measure.weightUnit.contains(thisUnit)
    
    def isVolume : Boolean = Measure.volumeUnit.contains(thisUnit)
    
    def isPieces : Boolean = thisUnit == "pieces"
    
    def isCups : Boolean = thisUnit == "cups"
    
/* Converts the current Measure to an equivalent quantity in newUnit, if newUnit is supported. 
 * The new unit is given as a String. 'convert' might use density and/or gramsPerPiece, 
 * depending on the conversion performed. 
 */    
    def convert(newUnit: String, density: Double, gramsPerPiece: Option[Double]) : Measure = { 
      var converted = this                                      
      if (this.isVolume) {
        if (Measure.volumeUnit.contains(newUnit)) {
          val exponent = Measure.volumeUnit.indexOf(thisUnit) - Measure.volumeUnit.indexOf(newUnit) 
          thisQuantity = thisQuantity*scala.math.pow(10,-exponent)
          thisUnit = newUnit
          converted = new Measure(thisQuantity, thisUnit)
        } else if (Measure.weightUnit.contains(newUnit)) {
          converted = fromVolumeToGrams(density, gramsPerPiece)
          converted = convert(newUnit, density, gramsPerPiece)
        } else if (newUnit == "cups") {
          converted = fromVolumeToCups(density, gramsPerPiece)
        } else if (newUnit == "pieces") {
          converted = fromVolumeToGrams(density, gramsPerPiece)
          converted = fromGramsToPieces(gramsPerPiece)
        }
      return converted
     }
      else if (this.isWeight) {
        if (Measure.weightUnit.contains(newUnit)) {
          val exponent = Measure.weightUnit.indexOf(thisUnit) - Measure.weightUnit.indexOf(newUnit) 
          thisQuantity = thisQuantity*scala.math.pow(10,-exponent)
          thisUnit = newUnit
          converted = new Measure(thisQuantity, thisUnit)
        } else if (Measure.volumeUnit.contains(newUnit)) {
          converted = fromWeightToMl(density, gramsPerPiece)
          converted = convert(newUnit, density, gramsPerPiece)
        } else if (newUnit == "cups") {
          converted = fromWeightToMl(density, gramsPerPiece)
          converted = fromVolumeToCups(density, gramsPerPiece)
        } else if (newUnit == "pieces") {
          converted = convert("grams",density, gramsPerPiece)
          converted = fromGramsToPieces(gramsPerPiece)
        }
      return converted
     }
      else if (this.isCups) {
          if (Measure.volumeUnit.contains(newUnit)) {
            converted = fromCupsToMl
            converted = convert(newUnit, density, gramsPerPiece)
          } else if (Measure.weightUnit.contains(newUnit)) {
            converted = fromCupsToMl
            converted = fromVolumeToGrams(density, gramsPerPiece)
            converted = convert(newUnit, density, gramsPerPiece)
          } else if (newUnit == "pieces") {
          converted = fromCupsToMl()
          converted = fromVolumeToGrams(density, gramsPerPiece)
          converted = fromGramsToPieces(gramsPerPiece)
        }
       return converted
      }
      else if (this.isPieces) {
          if (Measure.volumeUnit.contains(newUnit)) {
            converted = fromPiecesToGrams(gramsPerPiece)
            if (converted.unit != "pieces"){
              converted = fromWeightToMl(density, gramsPerPiece)
              converted = convert(newUnit, density, gramsPerPiece)
            }
          } else if (Measure.weightUnit.contains(newUnit)) {
            converted = fromPiecesToGrams(gramsPerPiece)
            if (converted.unit != "pieces"){
              converted = convert(newUnit, density, gramsPerPiece)
            }
          } else if (newUnit == "cups") {
          converted = fromPiecesToGrams(gramsPerPiece)
          if (converted.unit != "pieces"){
            converted = fromWeightToMl(density, gramsPerPiece)
            converted = fromVolumeToCups(density, gramsPerPiece)
          }
        }
       return converted        
      }
      else  converted
    }
    
    
    private def fromWeightToMl(density: Double, gramsPerPiece: Option[Double]) : Measure = {
      thisQuantity = this.convert("grams", density, gramsPerPiece).thisQuantity/density
      thisUnit = "ml"
      new Measure(thisQuantity, thisUnit)
    }
    
    private def fromVolumeToGrams(density: Double, gramsPerPiece: Option[Double]) : Measure = {
      thisQuantity = this.convert("ml", density, gramsPerPiece).thisQuantity*density
      thisUnit = "grams"
      new Measure(thisQuantity, thisUnit)
    }
    
    private def fromVolumeToCups(density: Double, gramsPerPiece: Option[Double]) : Measure = {
      thisQuantity = this.convert("ml", density, gramsPerPiece).thisQuantity/Measure.cup
      thisUnit = "cups"
      new Measure(thisQuantity, thisUnit)
    }
    
    private def fromCupsToMl() : Measure = {
      thisQuantity = thisQuantity*Measure.cup
      thisUnit = "ml"
      new Measure(thisQuantity, thisUnit)
    }
      
    private def fromPiecesToGrams(gramsPerPiece: Option[Double]) : Measure = {
       gramsPerPiece match {
         case Some(g) => {
           if (g!=0.0){
           thisQuantity = thisQuantity*g
           thisUnit = "grams"
           }
           return new Measure(thisQuantity, thisUnit)
           }
        case None => return new Measure(thisQuantity, thisUnit)
      }
    }
      
    private def fromGramsToPieces(gramsPerPiece: Option[Double]) : Measure = {
      gramsPerPiece match {
        case Some(g) => {
            thisQuantity = thisQuantity/g
            thisUnit = "pieces"
            return new Measure(thisQuantity, thisUnit)
            }
        case None => return new Measure(thisQuantity, thisUnit)
      }
    }
    
}


/* This object stores the supported units of weight and units of volume, 
 * stored in immutable vectors. It also stores the constant value of cup
 * in milliliters, as found from https://en.wikipedia.org/wiki/Cup_(unit)#Legal_cup.
 */
object Measure{
    val weightUnit = Vector("kg", "hg", "dag", "grams", "dg", "cg", "mg")
    
    val volumeUnit = Vector("kl", "hl", "dal", "liters", "dl", "cl", "ml")
    
    val cup = 240.0 //ml <- This holds for every food, it is not a specific property
}