For the PBTests, I selected the `Position` class because its transformation methods (getLeft, getRight, getUp, getDown) have clear mathematical properties that can be expressed independently of specific inputs.

Using jqwik, I generated hundreds of random integer coordinate pairs (x, y) within a defined range, and verified that universal properties always hold. Examples include reversibility (moving left then right returns to the original position), consistency of transformations (getLeft reduces x by exactly 1), and stability of opposite operations (up followed by down returns the original).

Integers were chosen instead of doubles to avoid floating-point precision issues, since the class uses exact equality (`==`) in its equals() method.

These property-based tests complement the BlackBox tests by checking general invariants over a large input space, increasing confidence in the correctness of the class.
