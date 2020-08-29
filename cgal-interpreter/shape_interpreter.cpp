#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include <CGAL/Projection_traits_xy_3.h>
#include <CGAL/Delaunay_triangulation_2.h>
#include <CGAL/Triangulation_2.h>
#include <CGAL/draw_triangulation_2.h>
#include <fstream>


typedef CGAL::Exact_predicates_inexact_constructions_kernel K;
typedef CGAL::Projection_traits_xy_3<K>  Gt;
typedef CGAL::Delaunay_triangulation_2<Gt> Delaunay;

typedef CGAL::Triangulation_2<K>                            Triangulation;
typedef Triangulation::Point                                Point;

int main() {
  std::ifstream in("C:\\dev\\CGAL-5.0.3\\examples\\Triangulation_2\\data\\triangulation_prog1.cin");
  std::istream_iterator<Point> begin(in);
  std::istream_iterator<Point> end;

  Triangulation t;
  t.insert(begin, end);
  CGAL::draw(t);

  return EXIT_SUCCESS;
}
