# AdBlockerWebview
An ordinary webview that can block basic ads.

[![](https://jitpack.io/v/islamdidarmd/AdBlockerWebview.svg)](https://jitpack.io/#islamdidarmd/AdBlockerWebview)

## Installation (with Gradle)
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
   allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency

```groovy
dependencies {
		implementation 'com.github.islamdidarmd:AdBlockerWebview:$latest_version'
	}
```

### How to use
Add AdBlockerWebview In your layout

```xml
    <com.islamdidarmd.adblockerwebview.AdBlockerWebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```
In your Activity or Fragment

```Kotlin
 AdBlockerUtil.getInstance().initialize(context)
 webview.setAdBlockerEnabled(true)
 webview.loadUrl("https://google.com")
```

You can also extend `AdBlockerWebView` to include additional functionality

For a complete example, see the sample app
