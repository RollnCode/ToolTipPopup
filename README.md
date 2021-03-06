# TooltipPopup Library

----------
Simple ToolTip Popup that will be shown near your view.
## Getting Started
### Dependency
Include the dependency [Download (.aar)](https://github.com/RollnCode/ToolTipPopup/blob/master/app/libs/tooltippopup.aar) and place it in your libs directory:
```groovy
allprojects {
    repositories {
        jcenter()
        flatDir {
            dirs 'libs'
        }
    }
}

// ...

dependencies {
    compile (name:'tooltippopup', ext:'aar')
    compile 'com.android.support:appcompat-v7:24.2.1'
    compile 'com.github.bumptech.glide:glide:3.7.0' // you need to include this too
}
```

### Usage
You need to use TooltipPopup.Builder to make new Tooltip and show it. Just set your view in TooltipPopup.Builder constructor and an image that will be shown like Tooltip. You can also use setters to add some params. Call show() to show your Tooltip.
The image can be like this:
![enter image description here](https://github.com/RollnCode/ToolTipPopup/blob/master/app/src/main/res/drawable/tooltip_dots.png)
