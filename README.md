# TachiyomiImageSplitter

Sometimes when viewing images on old tablet some images are too big.
(Especially when reading webtoons) so I made this app that will look inside tachiyomi folder and
will split images based on screen size.

It will only work if you download chapters.

Path should be set in your home `Tachiyomi` folder.


# Splitting

By default the application is splitting images in parallel on four threads.

In Safe mode it will do them one by one.

# Clean

Not working: this button was supposed to remove `.nomedia` files which prevent seeing files
when mounting tablet on a PC.

But `.nomedia` can't be accessed normally. 


# Installing

Some self generated apk are available [HERE](https://github.com/Lightjohn/TachiyomiImageSplitter/releases)

To install them you will need to enable *install from unknown source* 


# Note

Resizing images is not really solving memory issue while splitting makes the reading fluid and keeps quality

Which is why I removed resize button
