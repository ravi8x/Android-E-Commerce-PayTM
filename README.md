Android E-Commerce app - PayTM Gateway
===================
Android simple e-commerce with PayTM payment gateway integrated.

| [Tutorial](https://www.androidhive.info/2019/02/android-integrating-paytm-payment-gateway-ecommerce-app/)      |  [Apk](http://download.androidhive.info/apk/mart9-paytm.apk) | [Video Demo](https://www.youtube.com/watch?v=SSgG1t63MjM)|
|----------|--------|------|

![Android Ecommerce PayTM integration](https://www.androidhive.info/wp-content/uploads/2019/02/android-e-commerce-app-paytm-integration.png)

![Android Ecommerce PayTM integration](https://www.androidhive.info/wp-content/uploads/2019/02/android-e-commerce-app-paytm-demo.png)

Backend REST API
===================
This app uses the REST API built in Laravel to list down the products, manage orders and handling PayTM payment transactions.

Refer the [Laravel PayTM](https://github.com/ravi8x/Laravel-PayTM-Server) project to know the REST API used for this project.

Changing the REST endpoint
===================
Currenlty this repo uses the demo REST API provided. You can build the backend project and change the base url in **app/build.gradle** file.
```gradle
productFlavors {
        dev {
            buildConfigField "String", "BASE_URL", "\"https://demo.androidhive.info/paytm/public/api/\""
            buildConfigField "String", "PAYTM_CALLBACK_URL", "\"https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=%s\""
            buildConfigField "boolean", "IS_PATM_STAGIN", "true"
        }

        prod {
            buildConfigField "String", "BASE_URL", "\"https://demo.androidhive.info/paytm/public/api/\""
            buildConfigField "String", "PAYTM_CALLBACK_URL", "\"https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=%s\""
            buildConfigField "boolean", "IS_PATM_STAGIN", "false"
        }
    }
```