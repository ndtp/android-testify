"use strict";(self.webpackChunkdocs=self.webpackChunkdocs||[]).push([[8975],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>y});var a=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},o=Object.keys(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(a=0;a<o.length;a++)n=o[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var l=a.createContext({}),c=function(e){var t=a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=c(e.components);return a.createElement(l.Provider,{value:t},e.children)},d="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},m=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,o=e.originalType,l=e.parentName,p=s(e,["components","mdxType","originalType","parentName"]),d=c(n),m=r,y=d["".concat(l,".").concat(m)]||d[m]||u[m]||o;return n?a.createElement(y,i(i({ref:t},p),{},{components:n})):a.createElement(y,i({ref:t},p))}));function y(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=n.length,i=new Array(o);i[0]=m;var s={};for(var l in t)hasOwnProperty.call(t,l)&&(s[l]=t[l]);s.originalType=e,s[d]="string"==typeof e?e:r,i[1]=s;for(var c=2;c<o;c++)i[c]=n[c];return a.createElement.apply(null,i)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},2458:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>l,contentTitle:()=>i,default:()=>u,frontMatter:()=>o,metadata:()=>s,toc:()=>c});var a=n(7462),r=(n(7294),n(3905));const o={},i="Changing the Locale in a test",s={unversionedId:"recipes/locale",id:"version-2.0.0-alpha01/recipes/locale",title:"Changing the Locale in a test",description:"API 24+",source:"@site/versioned_docs/version-2.0.0-alpha01/recipes/2-locale.md",sourceDirName:"recipes",slug:"/recipes/locale",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/locale",draft:!1,editUrl:"https://github.com/ndtp/android-testify/tree/issue-12/docusaurus/docs/versioned_docs/version-2.0.0-alpha01/recipes/2-locale.md",tags:[],version:"2.0.0-alpha01",sidebarPosition:2,frontMatter:{},sidebar:"tutorialSidebar",previous:{title:"Taking a screenshot of an area less than that of the entire Activity",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/view-provider"},next:{title:"Changing the font scale in a test",permalink:"/android-testify/docs/2.0.0-alpha01/recipes/font-scale"}},l={},c=[{value:"API 24+",id:"api-24",level:2},{value:"API 23 or lower",id:"api-23-or-lower",level:2}],p={toc:c},d="wrapper";function u(e){let{components:t,...n}=e;return(0,r.kt)(d,(0,a.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"changing-the-locale-in-a-test"},"Changing the Locale in a test"),(0,r.kt)("h2",{id:"api-24"},"API 24+"),(0,r.kt)("p",null,"It is often desirable to test your View or Activity in multiple locales. Testify allows you to dynamically change the locale on a per-test basis. "),(0,r.kt)("p",null,"To begin, if you are targeting an emulator running Android API 24 or higher, your activity under test must implement the ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/230607acc598afe7d54f9618d55fdecd0da83800/Library/src/main/java/dev/testify/resources/TestifyResourcesOverride.kt"},"TestifyResourcesOverride")," interface. This allows Testify to attach a new ",(0,r.kt)("inlineCode",{parentName:"p"},"Context")," with the appropriate locale loaded. It is highly recommended that you employ a ",(0,r.kt)("em",{parentName:"p"},"test harness activity")," for this purpose. Please see the ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/230607acc598afe7d54f9618d55fdecd0da83800/Samples/Legacy/src/androidTest/java/dev/testify/sample/test/TestLocaleHarnessActivity.kt"},"TestHarnessActivity")," in the provided Sample."),(0,r.kt)("p",null,"With an Activity which implements ",(0,r.kt)("inlineCode",{parentName:"p"},"TestifyResourcesOverride"),", you can now invoke the ",(0,r.kt)("a",{parentName:"p",href:"https://github.com/ndtp/android-testify/blob/230607acc598afe7d54f9618d55fdecd0da83800/Library/src/main/java/dev/testify/ScreenshotRule.kt#L269"},"setLocale")," method on the ",(0,r.kt)("inlineCode",{parentName:"p"},"ScreenshotTestRule"),". ",(0,r.kt)("inlineCode",{parentName:"p"},"setLocale")," accepts any valid ",(0,r.kt)("a",{parentName:"p",href:"https://docs.oracle.com/javase/7/docs/api/java/util/Locale.html"},"Locale")," instance."),(0,r.kt)("p",null,(0,r.kt)("em",{parentName:"p"},"Example Test:")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"class TestLocaleActivityTest {\n\n    @get:Rule var rule = ScreenshotRule(\n        activityClass = TestLocaleHarnessActivity::class.java,\n        launchActivity = false,\n        rootViewId = R.id.harness_root\n    )\n\n    @ScreenshotInstrumentation\n    @TestifyLayout(R.layout.view_client_details)\n    @Test\n    fun testLocaleFrance() {\n        rule\n            .setLocale(Locale.FRANCE)\n            .assertSame()\n    }\n}\n")),(0,r.kt)("p",null,(0,r.kt)("em",{parentName:"p"},"Example Test Harness Activity")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"open class TestHarnessActivity : AppCompatActivity(), TestifyResourcesOverride {\n\n    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)\n\n        setContentView(FrameLayout(this).apply {\n            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)\n            id = R.id.harness_root\n        })\n    }\n\n    override fun attachBaseContext(newBase: Context?) {\n        super.attachBaseContext(newBase?.wrap())\n    }\n}\n")),(0,r.kt)("p",null,"Please read this excellent ",(0,r.kt)("a",{parentName:"p",href:"https://proandroiddev.com/change-language-programmatically-at-runtime-on-android-5e6bc15c758"},"blog post")," if you want to better understand how to dynamically adjust Locale in your app. Note that the Testify locale override support is intended for instrumentation testing only and does not provide a suitable solution for your production application."),(0,r.kt)("h2",{id:"api-23-or-lower"},"API 23 or lower"),(0,r.kt)("p",null,"On lower API levels, a test harness activity is not required. You are not required to implement ",(0,r.kt)("inlineCode",{parentName:"p"},"TestifyResourcesOverride"),", but doing so is not harmful."),(0,r.kt)("p",null,"To test with a provided locale, invoke the ",(0,r.kt)("inlineCode",{parentName:"p"},"setLocale")," method on ",(0,r.kt)("inlineCode",{parentName:"p"},"ScreenshotRule")),(0,r.kt)("p",null,(0,r.kt)("em",{parentName:"p"},"Example Test:")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-kotlin"},"class MainActivityScreenshotTest {\n\n    @get:Rule var rule = ScreenshotRule(MainActivity::class.java)\n\n    @ScreenshotInstrumentation\n    @TestifyLayout(R.layout.view_client_details)\n    @Test\n    fun testLocaleFrance() {\n        rule\n            .setLocale(Locale.FRANCE)\n            .assertSame()\n    }\n}\n")))}u.isMDXComponent=!0}}]);