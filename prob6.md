
# Rotation Matricies
Detailed instructions to submit ipynb files can be found on Piazza Post @17.
Please save your hw6.ipynb as a pdf and attach it to your written homework. This combined pdf will be submitted as hw6.pdf.
Submit both hw6.pdf and hw6.ipynb through glookup.


```python
import numpy as np
import matplotlib.pyplot as plt
%matplotlib inline
plt.rcParams['figure.figsize'] = (30.0, 6.5)
```

Fill in the function below so that it constructs a rotation matrix from an input angle, theta, and a scaling factor, r



```python
#Returns a rotation matrix for a desired rotation and scaling
# theta is the degrees of rotation
# r is the scaling factor of the rotation matrix
def rotation_matrix(theta,r):
   
    # Represent the rotation matrix
    r_matrix = np.array([[np.cos(theta), -1*np.sin(theta)],
                         [np.sin(theta), np.cos(theta)]])
  
    return r*r_matrix
```


```python
print (rotation_matrix(np.pi/2, 5))
```

    [[  3.06161700e-16  -5.00000000e+00]
     [  5.00000000e+00   3.06161700e-16]]


## Eigenvalues of Rotation Matricies
Use your matrix construction function to construct the specified matricies below. Fill in the #TODO lines with your code to do this. Note, it is best to represent the matrix as an np.array object since other code assumes the matricies will be in that form.  


```python
############################################
#Construct a matrix for theta= pi/4, r = 1

mat0= rotation_matrix(np.pi/4, 1)

#calculate the eigenvalues and eigenvectors of mat0

eig0, vec0= np.linalg.eig(mat0)

#Caculate the magnitude of the eigenvalues

mag0= np.absolute(eig0)

############################################
#construct a matrix for theta = pi/4, r=1.25

mat1= rotation_matrix(np.pi/4, 1.25)

#calculate the eigenvalues and eigenvectors for mat1

eig1, vec1= np.linalg.eig(mat1)

#Caculate the magnitude of the eigenvalues

mag1= np.absolute(eig1)

############################################
#construct a matrix for theta = pi/4, r=0.5

mat2= rotation_matrix(np.pi/4, 0.5)

#calculate the eigenvalues and eigenvectors for mat2

eig2, vec2= np.linalg.eig(mat2)

#Caculate the magnitude of the eigenvalues
mag2= np.absolute(eig2)

############################################


print ("mat0 eigenvalues: "+str(eig0))  
print ("mat0 eigenvalues magnitudes: "+str(mag0))
print ("mat1 eigenvalues: "+str(eig1))
print ("mat1 eigenvalues magnitudes: "+str(mag1))
print ("mat2 eigenvalues: "+str(eig2))
print ("mat2 eigenvalues magnitudes: "+str(mag2))

```

    mat0 eigenvalues: [ 0.70710678+0.70710678j  0.70710678-0.70710678j]
    mat0 eigenvalues magnitudes: [ 1.  1.]
    mat1 eigenvalues: [ 0.88388348+0.88388348j  0.88388348-0.88388348j]
    mat1 eigenvalues magnitudes: [ 1.25  1.25]
    mat2 eigenvalues: [ 0.35355339+0.35355339j  0.35355339-0.35355339j]
    mat2 eigenvalues magnitudes: [ 0.5  0.5]


## Rotation of Vectors Using Rotation Matricies 
Fill in the #TODO items to rotate the vector x1. Note, the plotting code assumes that the resulting vecotors are np.array objects


```python

x1=np.array([1,0])
#Apply mat0 once to x1
x1_r1= np.dot(mat0, x1)

#Apply mat0 twice to x1
x1_r2= np.dot(mat0, x1_r1)

#Apply mat0 three times to x1
x1_r3= np.dot(mat0, x1_r2)




#this code will plot your rotated state vectors
fig =plt.figure()
plt.subplot(141)
plt.arrow(0,0,x1[0],x1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('Original Vector')
plt.subplot(142)
plt.arrow(0,0,x1_r1[0],x1_r1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('1 Rotation')
plt.subplot(143)
plt.arrow(0,0,x1_r2[0],x1_r2[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('2 Rotations')
plt.subplot(144)
plt.arrow(0,0,x1_r3[0],x1_r3[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('3 Rotations')
plt.show()

```


![png](output_8_0.png)


Now we will see what happens when we rotate this state vector by our second rotation matrix


```python
x1=np.array([1,0])
#Apply mat1 once to x1
x1_r1= np.dot(mat1, x1)

#Apply mat1 twice to x1
x1_r2= np.dot(mat1, x1_r1)

#Apply mat1 three times to x1
x1_r3= np.dot(mat1, x1_r2)




#this code will plot your rotated state vectors
plt.close('all')
plt.figure()
plt.subplot(141)
plt.arrow(0,0,x1[0],x1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('Original Vector')
plt.subplot(142)
plt.arrow(0,0,x1_r1[0],x1_r1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('1 Rotation')
plt.subplot(143)
plt.arrow(0,0,x1_r2[0],x1_r2[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('2 Rotations')
plt.subplot(144)
plt.arrow(0,0,x1_r3[0],x1_r3[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('3 Rotations')
plt.show()

```


![png](output_10_0.png)


Now let's rotate x1 with mat2


```python

x1=np.array([1,0])
#Apply mat2 once to x1
x1_r1= np.dot(mat2, x1)

#Apply mat2 twice to x1
x1_r2= np.dot(mat2, x1_r1)

#Apply mat2 three times to x1
x1_r3= np.dot(mat2, x1_r2)




#this code will plot your rotated state vectors
plt.close('all')
plt.figure()
plt.subplot(141)
plt.arrow(0,0,x1[0],x1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('Original Vector')
plt.subplot(142)
plt.arrow(0,0,x1_r1[0],x1_r1[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('1 Rotation')
plt.subplot(143)
plt.arrow(0,0,x1_r2[0],x1_r2[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('2 Rotations')
plt.subplot(144)
plt.arrow(0,0,x1_r3[0],x1_r3[1])
plt.ylim([-2,2])
plt.xlim([-2,2])
plt.title('3 Rotations')
plt.show()

```


![png](output_12_0.png)



```python

```
