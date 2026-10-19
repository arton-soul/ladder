Add-Type -AssemblyName System.Drawing

$width = 512
$height = 512
$bmp = New-Object System.Drawing.Bitmap($width, $height, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
$g.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
$g.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality

# Clear transparent
$g.Clear([System.Drawing.Color]::Transparent)

# Background squircle / rounded rect
$rect = New-Object System.Drawing.Rectangle(16, 16, 480, 480)
$radius = 100
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddArc($rect.X, $rect.Y, $radius, $radius, 180, 90)
$path.AddArc($rect.Right - $radius, $rect.Y, $radius, $radius, 270, 90)
$path.AddArc($rect.Right - $radius, $rect.Bottom - $radius, $radius, $radius, 0, 90)
$path.AddArc($rect.X, $rect.Bottom - $radius, $radius, $radius, 90, 90)
$path.CloseFigure()

# Background gradient: vibrant blue to deep royal indigo
$pt1 = New-Object System.Drawing.Point(0, 0)
$pt2 = New-Object System.Drawing.Point(512, 512)
$gradBrush = New-Object System.Drawing.Drawing2D.LinearGradientBrush($pt1, $pt2, [System.Drawing.Color]::FromArgb(255, 33, 150, 243), [System.Drawing.Color]::FromArgb(255, 13, 71, 161))
$g.FillPath($gradBrush, $path)

# Subtle overlay light arc
$overlayPath = New-Object System.Drawing.Drawing2D.GraphicsPath
$overlayPath.AddArc(16, 16, 480, 300, 180, 180)
$overlayBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(28, 255, 255, 255))
$g.FillPath($overlayBrush, $overlayPath)

# Shadow for ladder
$shadowBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(70, 0, 20, 60))
$shadowPen = New-Object System.Drawing.Pen($shadowBrush, 28)
$shadowPen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
$shadowPen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round

# Draw ladder shadow
$offsetY = 12
$g.DrawLine($shadowPen, 180, 110 + $offsetY, 180, 410 + $offsetY)
$g.DrawLine($shadowPen, 332, 110 + $offsetY, 332, 410 + $offsetY)
$g.DrawLine($shadowPen, 180, 160 + $offsetY, 332, 160 + $offsetY)
$g.DrawLine($shadowPen, 180, 230 + $offsetY, 332, 230 + $offsetY)
$g.DrawLine($shadowPen, 180, 300 + $offsetY, 332, 300 + $offsetY)
$g.DrawLine($shadowPen, 180, 370 + $offsetY, 332, 370 + $offsetY)

# Draw white ladder
$ladderPen = New-Object System.Drawing.Pen([System.Drawing.Color]::White, 26)
$ladderPen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
$ladderPen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round

# Vertical rails
$g.DrawLine($ladderPen, 180, 110, 180, 410)
$g.DrawLine($ladderPen, 332, 110, 332, 410)

# Rungs
$g.DrawLine($ladderPen, 180, 160, 332, 160)
$g.DrawLine($ladderPen, 180, 230, 332, 230)
$g.DrawLine($ladderPen, 180, 300, 332, 300)
$g.DrawLine($ladderPen, 180, 370, 332, 370)

# Golden Star / Achievement badge at top
$starBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 215, 64))
$goldPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(255, 255, 179, 0), 3)

# Draw top star crown on the ladder (center at 256, 92)
$starPoints = @(
    New-Object System.Drawing.PointF(256, 68),
    New-Object System.Drawing.PointF(264, 84),
    New-Object System.Drawing.PointF(282, 85),
    New-Object System.Drawing.PointF(268, 97),
    New-Object System.Drawing.PointF(273, 115),
    New-Object System.Drawing.PointF(256, 103),
    New-Object System.Drawing.PointF(239, 115),
    New-Object System.Drawing.PointF(244, 97),
    New-Object System.Drawing.PointF(230, 85),
    New-Object System.Drawing.PointF(248, 84)
)
$g.FillPolygon($starBrush, $starPoints)
$g.DrawPolygon($goldPen, $starPoints)

$destPath = "Doc\store_listing\assets\icon_512x512.png"
$bmp.Save($destPath, [System.Drawing.Imaging.ImageFormat]::Png)

$g.Dispose()
$bmp.Dispose()
Write-Output "Successfully generated: $destPath"
