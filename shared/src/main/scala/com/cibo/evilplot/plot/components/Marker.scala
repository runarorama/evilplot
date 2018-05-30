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

package com.cibo.evilplot.plot.components

import com.cibo.evilplot.geometry.{Drawable, Extent}
import com.cibo.evilplot.plot.Plot
import com.cibo.evilplot.plot.aesthetics.Theme

final case class Marker(
  position: Position,
  f: (Extent => Drawable),
  size: Extent,
  x: Double = 0,
  y: Double = 0
) extends PlotComponent {

  override def size(plot: Plot): Extent = size

  def render(plot: Plot, extent: Extent)(implicit theme: Theme): Drawable = {
    val drawable = f(extent)
    position match {
      case Position.Top | Position.Bottom =>
        val xoffset = plot.xtransform(plot, extent)(x) - (drawable.extent.width / 2)
        drawable.translate(x = xoffset)
      case Position.Left | Position.Right =>
        val yoffset = plot.ytransform(plot, extent)(y) - (drawable.extent.height / 2)
        drawable.translate(y = yoffset)
      case Position.Overlay | Position.Background =>
        val yoffset = plot.ytransform(plot, extent)(y) - (drawable.extent.height / 2)
        val xoffset = plot.xtransform(plot, extent)(x) - (drawable.extent.width / 2)
        drawable.translate(x = xoffset, y = yoffset)
    }
  }
}