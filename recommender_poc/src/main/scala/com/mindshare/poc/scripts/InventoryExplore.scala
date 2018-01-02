package com.mindshare.poc.scripts

import scala.util.{Success, Try}

object InventoryExplore {
  private val commonProps = Set("888", "112", "159", "364", "categoryid", "764", "available", "790", "283")
  private val propsMapping = Map(
    "categoryid" -> "categoryid",
    "available" -> "available",
    "283" -> "description",
    "888" -> "title",
    "790" -> "price",
    "685" -> "discount"
  )


  private val numericProps = "381,455,1035,881,313,929,1082,141,870,650,670,668,55,212,255,805,155,903,495,703,967,428,923,656,221,943,397,111,622,162,389,203,249,246,1100,790,685,677,220,218,485,168,1072,150,626,239".split(",").toSet

  private val pricePropsCandidates = Set("141", "397", "805", "929", "111", "790")

  private val numericRegex = "^n\\d+\\.\\d+$".r

  case class RawItem(id: String,
                     parentId: String,
                     title: String,
                     description: String,
                     price: Double,
                     discount: Option[Double],
                     available: Int,
                     category: List[String],
                     numericProps: Map[String, Double],
                     metaProps: Map[String, String]
                    )

  case class ItemStub(id: String, props: List[String], values: List[String]) {
    val propMap = props.zip(values).toMap

    override def toString(): String = {
      val b = new StringBuilder
      b ++= "---------------> id: "
      b ++= id
      b ++= " props: "
      b ++= props.length.toString
      b ++= " unique props: "
      b ++= props.distinct.length.toString
      b ++= " values: "
      b ++= values.length.toString
      b += '\n'
      props.zip(values).toMap.toList.sortBy(-_._2.length).zipWithIndex.foreach{
        case ((p, v), idx) =>
          b += '\t'
          b ++= idx.toString
          b += '\t'
          b ++= p
          b += '\t'
          b ++= v
          b += '\n'
      }
      b.result()
    }
  }

  private def readInventory(file: String): Iterator[String] = {
    scala.io.Source.fromFile(file).getLines()
  }

  private def parse(str: String): ItemStub = {
    val Array(_, _, itemId, props, values) = str.split(",")

    ItemStub(itemId, props.split("\\|").toList, values.split("\\|").toList)
  }

  private def parseItem(str: String): Try[RawItem] = {
    val itemStub = parse(str)

    val parsedItem = RawItem(
      id = itemStub.id,
      parentId = "",
      title = itemStub.propMap("888"),
      description = itemStub.propMap("283"),
      price = itemStub.propMap("790").tail.toDouble,
      discount = itemStub.propMap.get("685").map(_.tail.toDouble),
      available = itemStub.propMap("available").toInt,
      category = itemStub.propMap("categoryid") :: Nil,
      numericProps = itemStub.propMap.collect{case (k, v) if numericProps.contains(k) => k -> v.tail.toDouble},
      metaProps = itemStub.propMap.filter(x => !numericProps.contains(x._1) && !propsMapping.contains(x._1))
    )

    Success(parsedItem)
  }

  private var globalSet = Set.empty[String]

  def main(args: Array[String]): Unit = {

    val fileName = "/Users/vitalii.antoniuk/projects/ml_experiments/temp/aggregated.csv"
    val inventoryIter = readInventory(fileName)

    val r = inventoryIter.drop(1).map(parseItem).take(10)

//    val r = inventoryIter.drop(1).map(parse).foldLeft(Map.empty[String, List[Double]])({ (acc, item) =>
//      val propMap = item.propMap.filter(x => numericProps(x._1)).map(x => x._1 -> Try(x._2.tail.toDouble).toOption)
//      propMap.map{
//        case (k, Some(v)) => k -> Some(v / propMap("790").get.max(1.0)).filter(_ < v)
//        case (k, None) =>
//          println(s"itemId: ${item.id} prop: $k value ${item.propMap(k)} ")
//          globalSet += k
//          k -> None
//      }.collect{case (k, Some(v)) => k -> v}.foldLeft(acc){ case (accum, (k, v)) =>
//        val vals = accum.getOrElse(k, Nil)
//          accum.updated(k, v :: vals)
//      }
//    })

//    val r = inventoryIter.drop(1).map(parse).foldLeft(List[Set[String]]())({ (acc, item) =>
//      item.propMap.filter({case (k, v) => k != "790" && v.contains(item.propMap("790"))}).keySet :: acc
//    }).foldLeft(Set.empty[String])(_ intersect _)
//    val r = inventoryIter.slice(1,30).map(parse).foreach(println)

//    println(r.map({case (k, v) => k -> (v.sum / v.length, v.min, v.max, v.length)}).toList.sortBy(- _._2._1).mkString("\n"))
    r.foreach(println)

    println(globalSet)
  }
}