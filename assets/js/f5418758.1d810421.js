"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[550],{3905:(e,t,n)=>{n.d(t,{Zo:()=>l,kt:()=>f});var o=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},i=Object.keys(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var p=o.createContext({}),c=function(e){var t=o.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},l=function(e){var t=c(e.components);return o.createElement(p.Provider,{value:t},e.children)},d="mdxType",m={inlineCode:"code",wrapper:function(e){var t=e.children;return o.createElement(o.Fragment,{},t)}},u=o.forwardRef((function(e,t){var n=e.components,r=e.mdxType,i=e.originalType,p=e.parentName,l=s(e,["components","mdxType","originalType","parentName"]),d=c(n),u=r,f=d["".concat(p,".").concat(u)]||d[u]||m[u]||i;return n?o.createElement(f,a(a({ref:t},l),{},{components:n})):o.createElement(f,a({ref:t},l))}));function f(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var i=n.length,a=new Array(i);a[0]=u;var s={};for(var p in t)hasOwnProperty.call(t,p)&&(s[p]=t[p]);s.originalType=e,s[d]="string"==typeof e?e:r,a[1]=s;for(var c=2;c<i;c++)a[c]=n[c];return o.createElement.apply(null,a)}return o.createElement.apply(null,n)}u.displayName="MDXCreateElement"},1422:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>p,contentTitle:()=>a,default:()=>m,frontMatter:()=>i,metadata:()=>s,toc:()=>c});var o=n(7462),r=(n(7294),n(3905));const i={},a="Set up testify-compose",s={unversionedId:"extensions/compose/setup",id:"version-2.0.0-rc02/extensions/compose/setup",title:"Set up testify-compose",description:"Prerequisites",source:"@site/versioned_docs/version-2.0.0-rc02/extensions/compose/1-setup.md",sourceDirName:"extensions/compose",slug:"/extensions/compose/setup",permalink:"/android-testify/docs/2.0.0-rc02/extensions/compose/setup",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-rc02/extensions/compose/1-setup.md",tags:[],version:"2.0.0-rc02",sidebarPosition:1,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Jetpack Compose",permalink:"/android-testify/docs/2.0.0-rc02/category/jetpack-compose"},next:{title:"Test a Composable",permalink:"/android-testify/docs/2.0.0-rc02/extensions/compose/test"}},p={},c=[{value:"Prerequisites",id:"prerequisites",level:3},{value:"Project configuration",id:"project-configuration",level:3},{value:"Manifest",id:"manifest",level:3}],l={toc:c},d="wrapper";function m(e){let{components:t,...n}=e;return(0,r.kt)(d,(0,o.Z)({},l,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"set-up-testify-compose"},"Set up testify-compose"),(0,r.kt)("a",{href:"https://search.maven.org/artifact/dev.testify/testify-compose"},(0,r.kt)("img",{alt:"Maven Central",src:"https://img.shields.io/maven-central/v/dev.testify/testify-compose?color=%236e40ed&label=dev.testify%3Atestify-compose"})),(0,r.kt)("h3",{id:"prerequisites"},"Prerequisites"),(0,r.kt)("p",null,"In order to use the Android Testify Compose extension, you must first configure the Testify Plugin on your project. To set up Testify for your project, please refer to the ",(0,r.kt)("a",{parentName:"p",href:"/android-testify/docs/2.0.0-rc02/get-started/setup"},"Getting Started")," guide."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Root build.gradle")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-groovy"},'buildscript {\n    repositories {\n        mavenCentral()\n    }\n    dependencies {\n        classpath "dev.testify:plugin:2.0.0-beta04"\n    }\n}\n')),(0,r.kt)("h3",{id:"project-configuration"},"Project configuration"),(0,r.kt)("p",null,"The Android Testify Compose extension is packaged as a separate artifact. You must add an ",(0,r.kt)("inlineCode",{parentName:"p"},"androidTestImplementation")," statement to your ",(0,r.kt)("inlineCode",{parentName:"p"},"build.gradle")," file to import it."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Application build.gradle")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-groovy"},'dependencies {\n    androidTestImplementation "dev.testify:testify-compose:2.0.0-beta04"\n    androidTestImplementation "androidx.test:rules:1.5.0"\n    androidTestImplementation "androidx.compose.ui:ui-test-junit4:1.4.3"\n}\n')),(0,r.kt)("h3",{id:"manifest"},"Manifest"),(0,r.kt)("p",null,"The Testify Compose Extension comes with a preconfigured test harness ",(0,r.kt)("inlineCode",{parentName:"p"},"Activity")," that is used to host your composables.\nTo use the ",(0,r.kt)("inlineCode",{parentName:"p"},"ComposableTestActivity")," in your test, you must declare it in your ",(0,r.kt)("inlineCode",{parentName:"p"},"AndroidManifest.xml"),"."),(0,r.kt)("p",null,"The ",(0,r.kt)("inlineCode",{parentName:"p"},"ComposableTestActivity")," is only referenced from Debug builds, so you can create a new file in the Debug source set (",(0,r.kt)("inlineCode",{parentName:"p"},"/src/debug/AndroidManifest.xml"),") with the following:"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-xml"},'<?xml version="1.0" encoding="utf-8"?>\n<manifest xmlns:android="http://schemas.android.com/apk/res/android">\n    <application>\n        \x3c!--suppress AndroidDomInspection --\x3e\n        <activity android:name="dev.testify.ComposableTestActivity" />\n    </application>\n</manifest>\n')),(0,r.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,r.kt)("div",{parentName:"div",className:"admonition-heading"},(0,r.kt)("h5",{parentName:"div"},(0,r.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,r.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,r.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"tip")),(0,r.kt)("div",{parentName:"div",className:"admonition-content"},(0,r.kt)("p",{parentName:"div"},"You need to use a ",(0,r.kt)("inlineCode",{parentName:"p"},"Theme.AppCompat")," theme (or descendant) with ",(0,r.kt)("inlineCode",{parentName:"p"},"ComposableTestActivity"),". ",(0,r.kt)("inlineCode",{parentName:"p"},"Theme.AppCompat.NoActionBar")," is a suitable choice."),(0,r.kt)("pre",{parentName:"div"},(0,r.kt)("code",{parentName:"pre",className:"language-xml"},'android:theme="@style/Theme.AppCompat.NoActionBar"\n')))))}m.isMDXComponent=!0}}]);