# Solutions:
## Part A - Fix current bugs
### Bug 1 - Layout does not look as expected
#### Solution: 
Added and fixed constraints to all the view elements. Also added margins so it follows the design expectations.

### Bug 2 - Validation is incorrect
#### Solution:
I modified the allFieldsValid() function in the following way:

In a separate if block I check when the name is valid and show the error if not. In a following block that returns the desired boolean I first check if both the email and the password is valid and show the errors if not. Then I check them 1 by 1 and show the designated error. In the last branch I return true if the name error is not shown.


Everytime the button is clicked I hide the error messages, so it always shows the current state.

### Bug 3 - Animation is looping incorrectly
#### Solution:
In setupAnimation() I set the setMaxFrame() to the desired value to make sure that it will stop there when it runs for the first time.
Then in the layout I set the animation's lottie_loop attribute to true and then added the animatorListener in the fragment. In the onAnimationRepeat() repeat function I set the min and max frame to expected values.


On onsStop() I reset the original frames, so the app won't crash when it's in the background.
When the user signs in and the the progress bar is displayed then the animation will pause. In case of errors the animation will continue running.

## Part B - Add 2 new screens
I'm following the MVVM architecture pattern. As a result the application is divided into three layers: view, view model and repositories.

Each view is a single activity and represented by fragments.

The navigation between fragments is managed by the Navigation Component. When the user hits the back button on the user account screen then the app will close. For an actual user there's no need to ba able to navigate back to the login screen.
Below the view layer I'm using RxJava to asychronously retrieve the data from the network. I subscribe to this data in the viewmodels and transfer it into lifecycle aware data (LiveData).

All the exceptions are displayed as Toast messages.

On the User account screen it's possible to swipe the screen and refresh, so the user is able to retry in case the product details didn't come through.

When the bearer token expires then the user is forced to login again by calling a restart extension method on the activity. It will call the finish() method and restart the activity which will show the login screen.


Used libraries:
⋅⋅* Koin
⋅⋅* Retrofit
⋅⋅* RxJava
⋅⋅* Espresso, Junit
⋅⋅* Mockito


I provided a sample unit and Espresso test suite as well.

