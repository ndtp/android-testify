"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7324],{3905:function(e,t,n){n.d(t,{Zo:function(){return s},kt:function(){return l}});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var p=r.createContext({}),u=function(e){var t=r.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},s=function(e){var t=u(e.components);return r.createElement(p.Provider,{value:t},e.children)},d={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,o=e.originalType,p=e.parentName,s=c(e,["components","mdxType","originalType","parentName"]),m=u(n),l=i,f=m["".concat(p,".").concat(l)]||m[l]||d[l]||o;return n?r.createElement(f,a(a({ref:t},s),{},{components:n})):r.createElement(f,a({ref:t},s))}));function l(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=n.length,a=new Array(o);a[0]=m;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c.mdxType="string"==typeof e?e:i,a[1]=c;for(var u=2;u<o;u++)a[u]=n[u];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},2179:function(e,t,n){n.r(t),n.d(t,{assets:function(){return s},contentTitle:function(){return p},default:function(){return l},frontMatter:function(){return c},metadata:function(){return u},toc:function(){return d}});var r=n(7462),i=n(3366),o=(n(7294),n(3905)),a=["components"],c={},p="Customizing the captured bitmap",u={unversionedId:"recipes/custom-bitmap",id:"recipes/custom-bitmap",title:"Customizing the captured bitmap",description:"Testify provides the setCaptureMethod() on ScreenshotRule which can be used to override the default mechanism for creating a bitmap",source:"@site/docs/recipes/16-custom-bitmap.md",sourceDirName:"recipes",slug:"/recipes/custom-bitmap",permalink:"/android-testify/docs/recipes/custom-bitmap",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/16-custom-bitmap.md",tags:[],version:"current",sidebarPosition:16,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Placing the keyboard focus on a specific view",permalink:"/android-testify/docs/recipes/keyboard-focus"},next:{title:"Providing a custom comparison method",permalink:"/android-testify/docs/recipes/custom-capture"}},s={},d=[],m={toc:d};function l(e){var t=e.components,n=(0,i.Z)(e,a);return(0,o.kt)("wrapper",(0,r.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h1",{id:"customizing-the-captured-bitmap"},"Customizing the captured bitmap"),(0,o.kt)("p",null,"Testify provides the ",(0,o.kt)("inlineCode",{parentName:"p"},"setCaptureMethod()")," on ",(0,o.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")," which can be used to override the default mechanism for creating a bitmap\nfrom the Activity under test. You can use ",(0,o.kt)("inlineCode",{parentName:"p"},"setCaptureMethod()")," to provide your own implementation of ",(0,o.kt)("inlineCode",{parentName:"p"},"CaptureMethod"),". The only requirement\nfor ",(0,o.kt)("inlineCode",{parentName:"p"},"CaptureMethod")," is that you return an ",(0,o.kt)("inlineCode",{parentName:"p"},"android.graphics.Bitmap")," instance. You can use any method you want to create a bitmap. You can\nalso use the provided ",(0,o.kt)("inlineCode",{parentName:"p"},"ScreenshotUtility")," to capture a bitmap and then modify it to your liking."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-kotlin"},'@ScreenshotInstrumentation\n@Test\nfun captureMethodExample() {\n    rule\n        .setCaptureMethod { activity, targetView ->\n            /* Return a Bitmap */\n            ScreenshotUtility().createBitmapFromView(activity, targetView).apply {\n                /* Wrap the Bitmap in a Canvas so we can draw on it */\n                Canvas(this).apply {\n                    /* Add a wordmark to the captured image */\n                    val textPaint = Paint().apply {\n                        color = Color.BLACK\n                        textSize = 50f\n                        isAntiAlias = true\n                    }\n                    this.drawText("<<Testify ${rule.testMethodName}>>", 50f, 2000f, textPaint)\n                }\n            }\n        }\n        .assertSame()\n}\n')))}l.isMDXComponent=!0}}]);