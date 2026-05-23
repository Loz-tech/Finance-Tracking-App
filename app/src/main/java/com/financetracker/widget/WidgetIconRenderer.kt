package com.financetracker.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorPath

object WidgetIconRenderer {
    fun render(context: Context, imageVector: ImageVector, sizePx: Int, tint: Color = Color.White): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val tintArgb = tint.toArgb()

        // Scale and draw vector paths
        val scaleX = sizePx.toFloat() / imageVector.viewportWidth
        val scaleY = sizePx.toFloat() / imageVector.viewportHeight
        canvas.save()
        canvas.scale(scaleX, scaleY)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            this.color = tintArgb
        }
        traverseAndDraw(imageVector.root, canvas, paint)

        canvas.restore()
        return bitmap
    }

    private fun traverseAndDraw(group: VectorGroup, canvas: Canvas, paint: Paint) {
        for (node in group) {
            when (node) {
                is VectorPath -> drawPath(node, canvas, paint)
                is VectorGroup -> traverseAndDraw(node, canvas, paint)
            }
        }
    }

    private fun drawPath(vectorPath: VectorPath, canvas: Canvas, paint: Paint) {
        val path = Path()
        val nodes = vectorPath.pathData
        var currentX = 0f
        var currentY = 0f
        var lastCx = 0f
        var lastCy = 0f

        for (node in nodes) {
            when (node) {
                is PathNode.MoveTo -> {
                    path.moveTo(node.x, node.y)
                    currentX = node.x
                    currentY = node.y
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.LineTo -> {
                    path.lineTo(node.x, node.y)
                    currentX = node.x
                    currentY = node.y
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.CurveTo -> {
                    path.cubicTo(
                        node.x1,
                        node.y1,
                        node.x2,
                        node.y2,
                        node.x3,
                        node.y3
                    )
                    lastCx = node.x2
                    lastCy = node.y2
                    currentX = node.x3
                    currentY = node.y3
                }
                is PathNode.ReflectiveCurveTo -> {
                    path.quadTo(node.x1, node.y1, node.x2, node.y2)
                    lastCx = node.x1
                    lastCy = node.y1
                    currentX = node.x2
                    currentY = node.y2
                }
                is PathNode.HorizontalTo -> {
                    path.lineTo(node.x, currentY)
                    currentX = node.x
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.VerticalTo -> {
                    path.lineTo(currentX, node.y)
                    currentY = node.y
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.RelativeMoveTo -> {
                    currentX += node.dx
                    currentY += node.dy
                    path.moveTo(currentX, currentY)
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.RelativeLineTo -> {
                    currentX += node.dx
                    currentY += node.dy
                    path.lineTo(currentX, currentY)
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.RelativeCurveTo -> {
                    val x1 = currentX + node.dx1
                    val y1 = currentY + node.dy1
                    val x2 = currentX + node.dx2
                    val y2 = currentY + node.dy2
                    val x3 = currentX + node.dx3
                    val y3 = currentY + node.dy3
                    path.cubicTo(x1, y1, x2, y2, x3, y3)
                    lastCx = x2
                    lastCy = y2
                    currentX = x3
                    currentY = y3
                }
                is PathNode.RelativeReflectiveCurveTo -> {
                    val x1 = currentX + node.dx1
                    val y1 = currentY + node.dy1
                    val x2 = currentX + node.dx2
                    val y2 = currentY + node.dy2
                    path.quadTo(x1, y1, x2, y2)
                    lastCx = x1
                    lastCy = y1
                    currentX = x2
                    currentY = y2
                }
                is PathNode.RelativeHorizontalTo -> {
                    currentX += node.dx
                    path.lineTo(currentX, currentY)
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.RelativeVerticalTo -> {
                    currentY += node.dy
                    path.lineTo(currentX, currentY)
                    lastCx = currentX
                    lastCy = currentY
                }
                is PathNode.ArcTo -> {
                    drawArcTo(path, currentX, currentY, node)
                    lastCx = currentX
                    lastCy = currentY
                    currentX = node.arcStartX
                    currentY = node.arcStartY
                }
                is PathNode.RelativeArcTo -> {
                    val endX = currentX + node.arcStartDx
                    val endY = currentY + node.arcStartDy
                    drawArcTo(
                        path, currentX, currentY,
                        node.horizontalEllipseRadius,
                        node.verticalEllipseRadius,
                        node.theta,
                        node.isMoreThanHalf,
                        node.isPositiveArc,
                        endX,
                        endY
                    )
                    lastCx = currentX
                    lastCy = currentY
                    currentX = endX
                    currentY = endY
                }
                is PathNode.QuadTo -> {
                    path.quadTo(node.x1, node.y1, node.x2, node.y2)
                    lastCx = node.x1
                    lastCy = node.y1
                    currentX = node.x2
                    currentY = node.y2
                }
                is PathNode.RelativeQuadTo -> {
                    val x1 = currentX + node.dx1
                    val y1 = currentY + node.dy1
                    val x2 = currentX + node.dx2
                    val y2 = currentY + node.dy2
                    path.quadTo(x1, y1, x2, y2)
                    lastCx = x1
                    lastCy = y1
                    currentX = x2
                    currentY = y2
                }
                is PathNode.ReflectiveQuadTo -> {
                    val cx = 2 * currentX - lastCx
                    val cy = 2 * currentY - lastCy
                    path.quadTo(cx, cy, node.x, node.y)
                    lastCx = cx
                    lastCy = cy
                    currentX = node.x
                    currentY = node.y
                }
                is PathNode.RelativeReflectiveQuadTo -> {
                    val cx = 2 * currentX - lastCx
                    val cy = 2 * currentY - lastCy
                    val endX = currentX + node.dx
                    val endY = currentY + node.dy
                    path.quadTo(cx, cy, endX, endY)
                    lastCx = cx
                    lastCy = cy
                    currentX = endX
                    currentY = endY
                }
                is PathNode.Close -> path.close()
            }
        }
        if (vectorPath.pathFillType == androidx.compose.ui.graphics.PathFillType.EvenOdd) {
            path.fillType = Path.FillType.EVEN_ODD
        }
        canvas.drawPath(path, paint)
    }

    private fun drawArcTo(path: Path, startX: Float, startY: Float, arcTo: PathNode.ArcTo) {
        drawArcTo(
            path = path,
            startX = startX,
            startY = startY,
            rx = arcTo.horizontalEllipseRadius,
            ry = arcTo.verticalEllipseRadius,
            theta = arcTo.theta,
            isMoreThanHalf = arcTo.isMoreThanHalf,
            isPositiveArc = arcTo.isPositiveArc,
            endX = arcTo.arcStartX,
            endY = arcTo.arcStartY
        )
    }

    @Suppress("LongParameterList")
    private fun drawArcTo(
        path: Path,
        startX: Float,
        startY: Float,
        rx: Float,
        ry: Float,
        theta: Float,
        isMoreThanHalf: Boolean,
        isPositiveArc: Boolean,
        endX: Float,
        endY: Float
    ) {
        if (rx == 0f || ry == 0f) {
            path.lineTo(endX, endY)
            return
        }

        val xAxisRotation = Math.toRadians(theta.toDouble())
        val cosPhi = Math.cos(xAxisRotation).toFloat()
        val sinPhi = Math.sin(xAxisRotation).toFloat()

        val dx = (startX - endX) / 2f
        val dy = (startY - endY) / 2f
        val x1p = cosPhi * dx + sinPhi * dy
        val y1p = -sinPhi * dx + cosPhi * dy

        var rxi = rx
        var ryi = ry
        val lambda = (x1p * x1p) / (rxi * rxi) + (y1p * y1p) / (ryi * ryi)
        if (lambda > 1f) {
            val scale = Math.sqrt(lambda.toDouble()).toFloat()
            rxi *= scale
            ryi *= scale
        }

        val sign = if (isMoreThanHalf != isPositiveArc) 1f else -1f
        val numerator = rxi * rxi * ryi * ryi - rxi * rxi * y1p * y1p - ryi * ryi * x1p * x1p
        val denominator = rxi * rxi * y1p * y1p + ryi * ryi * x1p * x1p
        val coef = sign * Math.sqrt((numerator / denominator).coerceAtLeast(0f).toDouble()).toFloat()
        val cxp = coef * (rxi * y1p) / ryi
        val cyp = coef * -(ryi * x1p) / rxi

        val midX = (startX + endX) / 2f
        val midY = (startY + endY) / 2f
        val cx = cosPhi * cxp - sinPhi * cyp + midX
        val cy = sinPhi * cxp + cosPhi * cyp + midY

        val ux = (x1p - cxp) / rxi
        val uy = (y1p - cyp) / ryi
        val vx = (-x1p - cxp) / rxi
        val vy = (-y1p - cyp) / ryi

        var startAngle = Math.toDegrees(Math.atan2(uy.toDouble(), ux.toDouble())).toFloat()
        var deltaAngle = Math.toDegrees(Math.atan2(vy.toDouble(), vx.toDouble())).toFloat() - startAngle
        if (!isPositiveArc && deltaAngle > 0f) deltaAngle -= 360f
        if (isPositiveArc && deltaAngle < 0f) deltaAngle += 360f

        val oval = RectF(cx - rxi, cy - ryi, cx + rxi, cy + ryi)
        path.arcTo(oval, startAngle, deltaAngle, false)
    }
}
