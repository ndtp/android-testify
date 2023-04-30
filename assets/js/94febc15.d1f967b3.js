"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[7610],{3905:(e,t,r)=>{r.d(t,{Zo:()=>d,kt:()=>m});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function s(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var c=n.createContext({}),l=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},d=function(e){var t=l(e.components);return n.createElement(c.Provider,{value:t},e.children)},u="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,o=e.originalType,c=e.parentName,d=s(e,["components","mdxType","originalType","parentName"]),u=l(r),f=a,m=u["".concat(c,".").concat(f)]||u[f]||p[f]||o;return r?n.createElement(m,i(i({ref:t},d),{},{components:r})):n.createElement(m,i({ref:t},d))}));function m(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var o=r.length,i=new Array(o);i[0]=f;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[u]="string"==typeof e?e:a,i[1]=s;for(var l=2;l<o;l++)i[l]=r[l];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},3396:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>u,frontMatter:()=>o,metadata:()=>s,toc:()=>l});var n=r(7462),a=(r(7294),r(3905));const o={},i="Write a test",s={unversionedId:"get-started/write-a-test",id:"version-2.0.0-alpha02/get-started/write-a-test",title:"Write a test",description:"Testify is a subclass of Android's ActivityTestRule. The testing framework launches the activity under test before each test method annotated with @Test and before any method annotated with @Before.",source:"@site/versioned_docs/version-2.0.0-alpha02/get-started/3-write-a-test.md",sourceDirName:"get-started",slug:"/get-started/write-a-test",permalink:"/android-testify/docs/2.0.0-alpha02/get-started/write-a-test",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-alpha02/get-started/3-write-a-test.md",tags:[],version:"2.0.0-alpha02",sidebarPosition:3,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Configure your emulator to run Testify tests",permalink:"/android-testify/docs/2.0.0-alpha02/get-started/configuring-an-emulator"},next:{title:"Update your baseline",permalink:"/android-testify/docs/2.0.0-alpha02/get-started/update-baseline"}},c={},l=[],d={toc:l};function u(e){let{components:t,...r}=e;return(0,a.kt)("wrapper",(0,n.Z)({},d,r,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h1",{id:"write-a-test"},"Write a test"),(0,a.kt)("p",null,"Testify is a subclass of Android's ",(0,a.kt)("a",{parentName:"p",href:"https://developer.android.com/reference/androidx/test/rule/ActivityTestRule.html"},(0,a.kt)("inlineCode",{parentName:"a"},"ActivityTestRule")),". The testing framework launches the activity under test before each test method annotated with ",(0,a.kt)("a",{parentName:"p",href:"https://junit.org/junit4/javadoc/latest/org/junit/Test.html"},(0,a.kt)("inlineCode",{parentName:"a"},"@Test"))," and before any method annotated with ",(0,a.kt)("a",{parentName:"p",href:"http://junit.sourceforge.net/javadoc/org/junit/Before.html"},(0,a.kt)("inlineCode",{parentName:"a"},"@Before")),". "),(0,a.kt)("p",null,"Each screenshot test method must be annotated with the ",(0,a.kt)("inlineCode",{parentName:"p"},"@ScreenshotInstrumentation")," annotation."),(0,a.kt)("p",null,"Within your test method, you can configure the ",(0,a.kt)("inlineCode",{parentName:"p"},"Activity")," as needed and call ",(0,a.kt)("inlineCode",{parentName:"p"},"assertSame()")," to capture and validate your UI. The framework handles shutting down the activity after the test finishes and all methods annotated with ",(0,a.kt)("a",{parentName:"p",href:"http://junit.sourceforge.net/javadoc/org/junit/After.html"},(0,a.kt)("inlineCode",{parentName:"a"},"@After"))," are run."),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-kotlin"},"@RunWith(AndroidJUnit4::class)\nclass MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @Test\n    fun default() {\n        rule.assertSame()\n    }\n}\n")))}u.isMDXComponent=!0}}]);