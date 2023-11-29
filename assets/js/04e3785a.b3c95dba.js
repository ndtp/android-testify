"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[5170],{3905:(e,t,r)=>{r.d(t,{Zo:()=>d,kt:()=>g});var n=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function o(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,i=function(e,t){if(null==e)return{};var r,n,i={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var l=n.createContext({}),p=function(e){var t=n.useContext(l),r=t;return e&&(r="function"==typeof e?e(t):o(o({},t),e)),r},d=function(e){var t=p(e.components);return n.createElement(l.Provider,{value:t},e.children)},c="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,i=e.mdxType,a=e.originalType,l=e.parentName,d=s(e,["components","mdxType","originalType","parentName"]),c=p(r),f=i,g=c["".concat(l,".").concat(f)]||c[f]||u[f]||a;return r?n.createElement(g,o(o({ref:t},d),{},{components:r})):n.createElement(g,o({ref:t},d))}));function g(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=r.length,o=new Array(a);o[0]=f;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[c]="string"==typeof e?e:i,o[1]=s;for(var p=2;p<a;p++)o[p]=r[p];return n.createElement.apply(null,o)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},7385:(e,t,r)=>{r.d(t,{Z:()=>a});var n=r(7294);function i(){return i=Object.assign?Object.assign.bind():function(e){for(var t=1;t<arguments.length;t++){var r=arguments[t];for(var n in r)Object.prototype.hasOwnProperty.call(r,n)&&(e[n]=r[n])}return e},i.apply(this,arguments)}const a=e=>{let{title:t,titleId:r,...a}=e;return n.createElement("svg",i({xmlns:"http://www.w3.org/2000/svg",width:16,height:16,viewBox:"0 0 16 16","aria-labelledby":r},a),t?n.createElement("title",{id:r},t):null,n.createElement("path",{style:{stroke:"none",fillRule:"nonzero",fill:"#9e80b5",fillOpacity:1},d:"M3 14a.947.947 0 0 1-.7-.3.947.947 0 0 1-.3-.7V3c0-.266.098-.5.3-.7.2-.202.434-.3.7-.3h4.652v1H3v10h10V8.348h1V13c0 .266-.098.5-.3.7-.2.202-.434.3-.7.3Zm3.367-3.652-.699-.715L12.301 3H8.652V2H14v5.348h-1V3.715Zm0 0"}))}},7751:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>p,contentTitle:()=>s,default:()=>f,frontMatter:()=>o,metadata:()=>l,toc:()=>d});var n=r(7462),i=(r(7294),r(3905)),a=r(7385);const o={},s="Configuring Testify to write to the SDCard",l={unversionedId:"recipes/sdcard",id:"version-2.0.0-rc02/recipes/sdcard",title:"Configuring Testify to write to the SDCard",description:"By default, Testify will write baseline images to the data/data/com.application.package/app_images/ directory of your device emulator.",source:"@site/versioned_docs/version-2.0.0-rc02/recipes/18-sdcard.md",sourceDirName:"recipes",slug:"/recipes/sdcard",permalink:"/android-testify/docs/2.0.0-rc02/recipes/sdcard",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/recipes/18-sdcard.md",tags:[],version:"2.0.0-rc02",sidebarPosition:18,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Providing a custom comparison method",permalink:"/android-testify/docs/2.0.0-rc02/recipes/custom-capture"},next:{title:"Multi-user support",permalink:"/android-testify/docs/2.0.0-rc02/recipes/multi-user"}},p={},d=[{value:"Configuring the Gradle Plugin to write to the SDCard",id:"configuring-the-gradle-plugin-to-write-to-the-sdcard",level:3},{value:"Configuring the Testify Library to write to the SDCard",id:"configuring-the-testify-library-to-write-to-the-sdcard",level:3},{value:"Configuring Testify to write to the SDCard using an environment variable",id:"configuring-testify-to-write-to-the-sdcard-using-an-environment-variable",level:3},{value:"Configuration for Firebase Test Lab",id:"configuration-for-firebase-test-lab",level:3}],c={toc:d},u="wrapper";function f(e){let{components:t,...r}=e;return(0,i.kt)(u,(0,n.Z)({},c,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"configuring-testify-to-write-to-the-sdcard"},"Configuring Testify to write to the SDCard"),(0,i.kt)("p",null,"By default, Testify will write baseline images to the ",(0,i.kt)("inlineCode",{parentName:"p"},"data/data/com.application.package/app_images/")," directory of your device emulator."),(0,i.kt)("p",null,"Some ",(0,i.kt)("a",{parentName:"p",href:"https://en.wikipedia.org/wiki/Continuous_integration"},"Continuous Integration ",(0,i.kt)(a.Z,{mdxType:"OpenNew"}))," environments may have access restrictions that prohibit Testify from writing files to this default location. In such situations, it may be preferrable to direct Testify to write baseline images to the device SDCard."),(0,i.kt)("h3",{id:"configuring-the-gradle-plugin-to-write-to-the-sdcard"},"Configuring the Gradle Plugin to write to the SDCard"),(0,i.kt)("p",null,"To instruct the Testify Gradle Plugin to output to the SDCard, you can use an input argument to set the ",(0,i.kt)("inlineCode",{parentName:"p"},"useSdCard")," ",(0,i.kt)("a",{parentName:"p",href:"https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties"},"project property ",(0,i.kt)(a.Z,{mdxType:"OpenNew"}))," using the ",(0,i.kt)("inlineCode",{parentName:"p"},"-P, --project-prop")," gradle environment option."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-bash"},"./gradlew :app:screenshotTest -PuseSdCard=true\n\n")),(0,i.kt)("h3",{id:"configuring-the-testify-library-to-write-to-the-sdcard"},"Configuring the Testify Library to write to the SDCard"),(0,i.kt)("p",null,"It is necessary to manually configure the Testify Library to write to the SDCard when directly running the tests as ",(0,i.kt)("a",{parentName:"p",href:"https://developer.android.com/training/testing/instrumented-tests"},"Android Instrumentation Tests ",(0,i.kt)(a.Z,{mdxType:"OpenNew"})),", and not using the Testify Gradle command line tasks. For example, this is necessary if you are using Android Studio's built-in instrumentation test runner, or a CI script."),(0,i.kt)("p",null,"To configure the Library to write to the SDCard, you must set the ",(0,i.kt)("inlineCode",{parentName:"p"},"useSdCard")," property to ",(0,i.kt)("inlineCode",{parentName:"p"},"true")," in your app's ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle"),"."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},"testify {\n    useSdCard = true\n}\n")),(0,i.kt)("h3",{id:"configuring-testify-to-write-to-the-sdcard-using-an-environment-variable"},"Configuring Testify to write to the SDCard using an environment variable"),(0,i.kt)("p",null,"You can optionally configure Testify to set the ",(0,i.kt)("inlineCode",{parentName:"p"},"useSdCard")," property from an environment variable. "),(0,i.kt)("p",null,"To begin, ",(0,i.kt)("a",{parentName:"p",href:"https://devconnected.com/set-environment-variable-bash-how-to/"},"define the environment variable ",(0,i.kt)(a.Z,{mdxType:"OpenNew"}))," ",(0,i.kt)("inlineCode",{parentName:"p"},"TESTIFY_USE_SDCARD"),"."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-bash"},'$ export TESTIFY_USE_SDCARD="true"\n')),(0,i.kt)("p",null,"Then, configure your app's ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," to read the ",(0,i.kt)("inlineCode",{parentName:"p"},"TESTIFY_USE_SDCARD")," environment variable."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'testify {\n    if (System.getenv("TESTIFY_USE_SDCARD") != null) {\n        useSdCard = System.getenv("TESTIFY_USE_SDCARD").toBoolean()\n    }\n}\n')),(0,i.kt)("h3",{id:"configuration-for-firebase-test-lab"},"Configuration for Firebase Test Lab"),(0,i.kt)("p",null,"The ",(0,i.kt)("a",{parentName:"p",href:"https://firebase.google.com/docs/test-lab"},"Firebase Test Lab ",(0,i.kt)(a.Z,{mdxType:"OpenNew"}))," will pass through environment variables using the ",(0,i.kt)("inlineCode",{parentName:"p"},"--environment-variables")," command.\nYou can use this method to pass the ",(0,i.kt)("inlineCode",{parentName:"p"},"TESTIFY_USE_SDCARD")," to Firebase."),(0,i.kt)("p",null,(0,i.kt)("a",{parentName:"p",href:"https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--environment-variables"},"https://cloud.google.com/sdk/gcloud/reference/firebase/test/android/run#--environment-variables")),(0,i.kt)("hr",null))}f.isMDXComponent=!0}}]);