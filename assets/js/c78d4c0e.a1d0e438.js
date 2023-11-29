"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[9885],{3905:(e,t,n)=>{n.d(t,{Zo:()=>c,kt:()=>m});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var l=r.createContext({}),d=function(e){var t=r.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},c=function(e){var t=d(e.components);return r.createElement(l.Provider,{value:t},e.children)},u="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},g=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,a=e.originalType,l=e.parentName,c=s(e,["components","mdxType","originalType","parentName"]),u=d(n),g=i,m=u["".concat(l,".").concat(g)]||u[g]||p[g]||a;return n?r.createElement(m,o(o({ref:t},c),{},{components:n})):r.createElement(m,o({ref:t},c))}));function m(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var a=n.length,o=new Array(a);o[0]=g;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[u]="string"==typeof e?e:i,o[1]=s;for(var d=2;d<a;d++)o[d]=n[d];return r.createElement.apply(null,o)}return r.createElement.apply(null,n)}g.displayName="MDXCreateElement"},7385:(e,t,n)=>{n.d(t,{Z:()=>a});var r=n(7294);function i(){return i=Object.assign?Object.assign.bind():function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var r in n)Object.prototype.hasOwnProperty.call(n,r)&&(e[r]=n[r])}return e},i.apply(this,arguments)}const a=e=>{let{title:t,titleId:n,...a}=e;return r.createElement("svg",i({xmlns:"http://www.w3.org/2000/svg",width:16,height:16,viewBox:"0 0 16 16","aria-labelledby":n},a),t?r.createElement("title",{id:n},t):null,r.createElement("path",{style:{stroke:"none",fillRule:"nonzero",fill:"#9e80b5",fillOpacity:1},d:"M3 14a.947.947 0 0 1-.7-.3.947.947 0 0 1-.3-.7V3c0-.266.098-.5.3-.7.2-.202.434-.3.7-.3h4.652v1H3v10h10V8.348h1V13c0 .266-.098.5-.3.7-.2.202-.434.3-.7.3Zm3.367-3.652-.699-.715L12.301 3H8.652V2H14v5.348h-1V3.715Zm0 0"}))}},7669:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>d,contentTitle:()=>s,default:()=>g,frontMatter:()=>o,metadata:()=>l,toc:()=>c});var r=n(7462),i=(n(7294),n(3905)),a=n(7385);const o={},s="Configuring Testify to run on Gradle managed device",l={unversionedId:"recipes/gmd",id:"version-2.0.0-rc02/recipes/gmd",title:"Configuring Testify to run on Gradle managed device",description:"Starting from API levels",source:"@site/versioned_docs/version-2.0.0-rc02/recipes/20-gmd.md",sourceDirName:"recipes",slug:"/recipes/gmd",permalink:"/android-testify/docs/2.0.0-rc02/recipes/gmd",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/recipes/20-gmd.md",tags:[],version:"2.0.0-rc02",sidebarPosition:20,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Multi-user support",permalink:"/android-testify/docs/2.0.0-rc02/recipes/multi-user"},next:{title:"Migrate to Testify 2.0",permalink:"/android-testify/docs/2.0.0-rc02/migration"}},d={},c=[{value:"Adding Gradle managed device configuration to build file",id:"adding-gradle-managed-device-configuration-to-build-file",level:3},{value:"Configuring the Testify library",id:"configuring-the-testify-library",level:3},{value:"Running screenshot tests",id:"running-screenshot-tests",level:3},{value:"Updating baselines",id:"updating-baselines",level:3},{value:"Sample",id:"sample",level:3}],u={toc:c},p="wrapper";function g(e){let{components:t,...n}=e;return(0,i.kt)(p,(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"configuring-testify-to-run-on-gradle-managed-device"},"Configuring Testify to run on Gradle managed device"),(0,i.kt)("p",null,"Starting from API levels\n27 ",(0,i.kt)("a",{parentName:"p",href:"https://developer.android.com/studio/test/gradle-managed-devices"},"Android Gradle Plugin allows you to configure virtual test devices in your project's Gradle files ",(0,i.kt)(a.Z,{mdxType:"OpenNew"})),".\nThis feature improves testing experience by delegating the task of starting, shutting down and\nmanaging emulator to the Gradle."),(0,i.kt)("p",null,"When using Gradle managed device user must\nuse ",(0,i.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/androidx/test/services/storage/TestStorage"},"Test storage service ",(0,i.kt)(a.Z,{mdxType:"OpenNew"})),"\nto save screenshots. Using this service Android Gradle Plugin would be able to save test output\nincluding screenshots and diffs to the ",(0,i.kt)("inlineCode",{parentName:"p"},"build")," folder"),(0,i.kt)("h3",{id:"adding-gradle-managed-device-configuration-to-build-file"},"Adding Gradle managed device configuration to build file"),(0,i.kt)("p",null,"To create new Gradle managed device add it's definition to the ",(0,i.kt)("inlineCode",{parentName:"p"},"build.gradle")," file:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'\nandroid {\n    testOptions {\n        managedDevices {\n            devices {\n                NAME_OF_THE_DEVICE(com.android.build.api.dsl.ManagedVirtualDevice) {\n                    device = "Pixel 2"\n                    apiLevel = 30\n                    systemImageSource = "aosp"\n                }\n            }\n        }\n    }\n}\n\n')),(0,i.kt)("p",null,"In order to receive screenshots after test execution enable ",(0,i.kt)("inlineCode",{parentName:"p"},"TestStorageService"),":"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},'\nandroid {\n    defaultConfig {\n        testInstrumentationRunnerArguments useTestStorageService: "true"\n    }\n}\n\ndependencies {\n    androidTestUtil("androidx.test.services:test-services:1.4.2")\n}\n')),(0,i.kt)("p",null,"With that changes applied couple of new Gradle tasks should be added to your project. Suppose, you\nnamed your device ",(0,i.kt)("inlineCode",{parentName:"p"},"tester"),", then ",(0,i.kt)("inlineCode",{parentName:"p"},"testerCheck")," and ",(0,i.kt)("inlineCode",{parentName:"p"},"testDebugAndroidTest")," tasks should be added to\nyour project."),(0,i.kt)("h3",{id:"configuring-the-testify-library"},"Configuring the Testify library"),(0,i.kt)("p",null,"You also need to instruct Testify to use Test storage to store screenshots and diffs. This could be\ndone using Testify plugin configuration:"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},"testify {\n    useTestStorage = true\n}\n")),(0,i.kt)("h3",{id:"running-screenshot-tests"},"Running screenshot tests"),(0,i.kt)("p",null,"To perform screenshot test verification run any of the new Gradle tasks, for\nexample ",(0,i.kt)("inlineCode",{parentName:"p"},"./gradlew testerDebugAndroidTest"),"."),(0,i.kt)("p",null,"Because we need to run specific Gradle task to execute our tests using Gradle managed device, we\ncannot use ",(0,i.kt)("inlineCode",{parentName:"p"},"screenshotPull")," task. When execution completed go to the\nmodule's ",(0,i.kt)("inlineCode",{parentName:"p"},"build/outputs/managed_device_android_test_additional_output/tester")," folder to get recorded\nscreenshots or diffs."),(0,i.kt)("h3",{id:"updating-baselines"},"Updating baselines"),(0,i.kt)("p",null,"Because we need to run specific Gradle task to execute our tests using Gradle managed device, we\ncannot use ",(0,i.kt)("inlineCode",{parentName:"p"},"screenshotRecord")," task. To generate new baseline there are two options:"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"apply necessary setting to the ",(0,i.kt)("inlineCode",{parentName:"li"},"ScreenshotRule"),":")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-kotlin"},"@get:Rule\nvar rule = ScreenshotRule(ClientListActivity::class.java)\n\n@ScreenshotInstrumentation\n@Test\nfun testMissingBaseline() {\n    rule\n        .setRecordModeEnabled(true)\n        .assertSame()\n}\n")),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},"or enable record mode in the ",(0,i.kt)("inlineCode",{parentName:"li"},"build.gradle")," file:")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-groovy"},"testify {\n    recordMode = true\n}\n")),(0,i.kt)("p",null,"Again we cannot use ",(0,i.kt)("inlineCode",{parentName:"p"},"screenshotPull")," task. When execution completed go to the\nmodule's ",(0,i.kt)("inlineCode",{parentName:"p"},"build/outputs/managed_device_android_test_additional_output/tester")," and copy recorded\nbaseline into the ",(0,i.kt)("inlineCode",{parentName:"p"},"androidTest/assets/screenshots/")," directory, for example: ",(0,i.kt)("inlineCode",{parentName:"p"},"androidTest/assets/screenshots/30-1080x1920@420dp-en_US")),(0,i.kt)("h3",{id:"sample"},"Sample"),(0,i.kt)("p",null,"Please check\nprovided ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/tree/main/Samples/Gmd"},"sample ",(0,i.kt)(a.Z,{mdxType:"OpenNew"})),"."))}g.isMDXComponent=!0}}]);