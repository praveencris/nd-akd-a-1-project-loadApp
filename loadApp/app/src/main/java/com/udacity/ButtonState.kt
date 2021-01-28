package com.udacity


enum class ButtonState {
    Clicked, Loading, Completed
}

/*
* I know this was already initially added in the starter code, however as a best practice, it would have been better to refactor this to an enum class, since enums are simpler to use. Sealed classes should be preferred only if the class is more complex and some of its objects may contain data.

However, a sealed class was still used here to show you an example of a simple sealed class.
* */