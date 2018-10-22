/*
 * Copyright (c) 2018, CiBO Technologies, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.cibo.evilplot.demo

import com.cibo.evilplot.colors._
import com.cibo.evilplot.geometry._
import com.cibo.evilplot.numeric._
import com.cibo.evilplot.{geometry, plot}
import com.cibo.evilplot.plot._
import com.cibo.evilplot.plot.aesthetics.DefaultTheme.{DefaultFonts, DefaultTheme}
import com.cibo.evilplot.plot.aesthetics.Theme
import com.cibo.evilplot.plot.components.{Legend, Marker, Position}
import com.cibo.evilplot.plot.renderers._

import scala.util.Random

object FastHistTest extends App{

  // val data = (0.0 to 3 by .25) ++ (3.0 to 5 by .05) ++ (5.0 to 8 by 1.0)


  implicit val theme: Theme = DefaultTheme.copy(
    fonts = DefaultFonts
      .copy(tickLabelSize = 14, legendLabelSize = 14, fontFace = "'Lato', sans-serif")
  )


  // val render = Histogram(data)//, 20)
  //   .standard()
  //   // .xbounds(-75, 225)
  //   // .ybounds(0, 15)
  //   // .vline(3.5, HTMLNamedColors.blue)
  //   .render(plotAreaSize)


  val plotAreaSize: Extent = Extent(1000, 600)

  def hist(data:Seq[Double], bins:Int, label:String):Unit = {

    val histOrig = Histogram(data,bins)
    val histBinned = BinnedPlot.continuous[Double](  // creates a histogram
                      data,
                      _.continuousBins(identity, bins)
                    )(_.histogram())

    List('orig -> histOrig, 'binned-> histBinned) foreach {case (k,v) => 
      // val render = histOrig.standard().xbounds(0,7)/*.ybounds(0, data.size)*/.render(plotAreaSize)
      val render = histOrig.standard().xbounds(2,7)/*.ybounds(0, data.size)*/.render(plotAreaSize)
      val file = new java.io.File(s"FastTest-${k.name}-$label-${bins}bins.png")
      render.write(file)
    }

  }


  // -- bugs
  // [x] 1. binning edge case        
  // [x] 2. standard breaks the edge cases???
  // [x] 3. ybounds is related to all this
  //     Its is all broken because the view bounds if filtering the data that is binned.  This shouldn't be possible.
  // [x]   lets try Chris's to see if it fails under the same problems
  // [x]  Chris has the same problem
  //
  // -- first fix
  // [x]  the fix should be deconflate plot range and data range (hard when both are auto too)
  // [x]  1. do the binning first
  // [x]  2. do the *viewing* NOT filtering data for binning
  // [x]  3. validate that the axis are correct.  maybe the shape and clipping are fixed in aaron and bill's hack but axis is off
  //
  // --remaining bugs
  // [x] should we clip histogram boxes by the view?
  // [-] y-axis tick marks are not on the top level overlay Position (probably Position.Left)
  // --remaining features
  // [x] implement plot ctx separate for original histogram function
  // [ ] hashed tests

  //---
  //
  val data = 1d to 10d by 1d
  val moreData = Seq.fill(10)(data).flatten
  hist(moreData, 2, "1-10")
  hist(moreData, 5, "1-10")

  val uniform = Seq.fill(10000)(Random.nextDouble()*10)
  hist(uniform, 5, "uniform")

  val normal = Seq.fill(10000)(Random.nextGaussian()*10)
  hist(normal, 5, "normal")

  val normal2 = Seq.fill(10000)(Random.nextGaussian()*1 + 4)
  hist(normal2, 50, "normal2")

  {
    val engines = "1.8  3.2  2.8  2.8  3.5  2.2  3.8  5.7  3.8  4.9  4.6  2.2  2.2  3.4  2.2  3.8  4.3  5.0  5.7  3.3  3.0  3.3  1.5  2.2  2.5  3.0  2.5  3.0  1.5  3.5  1.3  1.8  2.3  2.3  2.0  3.0  3.0  4.6  1.0  1.6  2.3  1.5  2.2  1.5  1.8  1.5  2.0  4.5  3.0  3.0  3.8  4.6  1.6  1.8  2.5  3.0  1.3  2.3  3.2  1.6  3.8  1.5  3.0  1.6  2.4  3.0  3.0  2.3  2.2  3.8  3.8  1.8  1.6  2.0  3.4  3.4  3.8  2.1  1.9  1.2  1.8  2.2  1.3  1.5  2.2  2.2  2.4  1.8  2.5  2.0  2.8  2.3  2.4".split("\\s+").map{_.toDouble}


    val render = Histogram(normal).standard().xLabel("engines").render(plotAreaSize)
    val file = new java.io.File(s"FastTest-engine-test.png")
    render.write(file)
  }

}

