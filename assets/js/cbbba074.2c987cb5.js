"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[9947],{3905:(e,t,r)=>{r.d(t,{Zo:()=>l,kt:()=>f});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var p=n.createContext({}),s=function(e){var t=n.useContext(p),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},l=function(e){var t=s(e.components);return n.createElement(p.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,i=e.originalType,p=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),d=s(r),m=a,f=d["".concat(p,".").concat(m)]||d[m]||u[m]||i;return r?n.createElement(f,o(o({ref:t},l),{},{components:r})):n.createElement(f,o({ref:t},l))}));function f(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=r.length,o=new Array(i);o[0]=m;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c[d]="string"==typeof e?e:a,o[1]=c;for(var s=2;s<i;s++)o[s]=r[s];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},3497:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>p,contentTitle:()=>o,default:()=>d,frontMatter:()=>i,metadata:()=>c,toc:()=>s});var n=r(7462),a=(r(7294),r(3905));const i={},o="Selecting an alternative capture method",c={unversionedId:"recipes/capture-method",id:"recipes/capture-method",title:"Selecting an alternative capture method",description:"Testify provides three bitmap capture method. Each method will capture slightly different results based primarily on API level.",source:"@site/docs/recipes/12-capture-method.md",sourceDirName:"recipes",slug:"/recipes/capture-method",permalink:"/android-testify/docs/recipes/capture-method",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/docs/recipes/12-capture-method.md",tags:[],version:"current",sidebarPosition:12,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Debugging with the Layout Inspector",permalink:"/android-testify/docs/recipes/layout-inspector"},next:{title:"Force software rendering",permalink:"/android-testify/docs/recipes/software-rendering"}},p={},s=[],l={toc:s};function d(e){let{components:t,...r}=e;return(0,a.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"selecting-an-alternative-capture-method"},"Selecting an alternative capture method"),(0,a.kt)("p",null,"Testify provides three bitmap capture method. Each method will capture slightly different results based primarily on API level."),(0,a.kt)("p",null,"The three capture methods available are:"),(0,a.kt)("p",null,"(1) Canvas: Render the view (and all of its children) to a given Canvas, using ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/view/View#draw(android.graphics.Canvas)"},"View.draw"),"\n(2) DrawingCache: Pulls the view's drawing cache bitmap using the deprecated ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/view/View#getDrawingCache()"},"View.getDrawingCache"),"\n(3) PixelCopy: Use Android's recommended ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/android/view/PixelCopy"},"PixelCopy")," API to capture the full screen, including elevation."),(0,a.kt)("p",null,"For legacy compatibility reasons, ",(0,a.kt)("inlineCode",{parentName:"p"},"DrawingCache")," mode is the default Testify capture method."),(0,a.kt)("p",null,"If you wish to select an alternative capture method, you can enable the experimental feature either in code, or in your manifest.\nAvailable features can be found in ",(0,a.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/230607acc598afe7d54f9618d55fdecd0da83800/Library/src/main/java/dev/testify/TestifyFeatures.kt#L30"},"TestifyFeatures")),(0,a.kt)("p",null,(0,a.kt)("strong",{parentName:"p"},"Code:")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"    @ScreenshotInstrumentation\n    @Test\n    fun testDefault() {\n        rule\n            .withExperimentalFeatureEnabled(TestifyFeatures.CanvasCapture)\n            .assertSame()\n    }\n")),(0,a.kt)("p",null,(0,a.kt)("strong",{parentName:"p"},"Manifest:")),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-xml"},'<manifest package="dev.testify.sample"\n    xmlns:android="http://schemas.android.com/apk/res/android">\n\n    <application>\n        <meta-data android:name="testify-canvas-capture" android:value="true" />\n    </application>\n\n</manifest>\n')))}d.isMDXComponent=!0}}]);