#include <CGAL/Exact_predicates_inexact_constructions_kernel.h>
#include <CGAL/Projection_traits_xy_3.h>
#include <CGAL/IO/Color.h>


#include <CGAL/Triangulation_2.h>
#include <CGAL/Delaunay_triangulation_2.h>
#include <CGAL/Delaunay_mesh_face_base_2.h>
#include <CGAL/Triangulation_face.h>
#include <CGAL/Triangulation_vertex.h>
#include <CGAL/Triangulation_vertex_base_with_info_2.h>
#include <CGAL/Triangulation_face_base_with_info_2.h>


#include <string>
#include <fstream>
#include <iostream>
#include <filesystem>
#include <unordered_map>

typedef CGAL::Exact_predicates_inexact_constructions_kernel K;
typedef CGAL::Projection_traits_xy_3<K>  Gt;
typedef CGAL::Triangulation_vertex_base_with_info_2<std::string, Gt> vb;
typedef CGAL::Triangulation_face_base_with_info_2<std::string, K> fb;
typedef CGAL::Triangulation_data_structure_2<vb, fb> tds;
typedef CGAL::Projection_traits_xy_3<K>  Gt;
typedef CGAL::Delaunay_triangulation_2<Gt,tds> Delaunay;

typedef CGAL::Point_3<K> Point;

typedef Delaunay::Vertex_handle Vertex_handle;
typedef Delaunay::Face_handle Face_handle;

// Its value type is Triangulation_2::Vertex
typedef Delaunay::Finite_vertices_iterator Finite_vertices_iterator;

namespace fs = std::experimental::filesystem;

int main() {
  std::ifstream in("C:\\dev\\CGAL-5.0.3\\examples\\Triangulation_2\\data\\triangulation_prog1.cin");
  std::istream_iterator<Point> begin(in);
  std::istream_iterator<Point> end;

  std::cout << fs::current_path() << std::endl;

  Delaunay dt(begin, end);

  std::unordered_map<Point, int> point_map;
  int i = 0;
  for (Vertex_handle v : dt.finite_vertex_handles()) {
    auto p = v->point();
    std::cout << i << ": " << p.x() << ", " << p.y() << ", " << p.z() << " " << std::endl;
    point_map[p] = i; i++;
  }

  i = 0;
  std::unordered_map<std::string, int> face_map;
  for (Face_handle f : dt.finite_face_handles()) {
    auto t = dt.triangle(f);
    f->info() = std::to_string(i);
    auto i1 = point_map[t.vertex(0)];
    auto i2 = point_map[t.vertex(1)];
    auto i3 = point_map[t.vertex(2)];

    std::cout << i << ": " << i1 << " " << i2 << " " << i3 << std::endl;
    face_map[f->info()] = i; i++;
  }

  for (auto e : dt.finite_edges()) {
    Face_handle f = e.first;
    auto fi = face_map[f->info()];
    auto line = dt.segment(f, e.second);
    auto si = point_map[line.start()];
    auto ei = point_map[line.end()];
    std::cout << fi << ": [" << si  << "-" << ei << "]" << std::endl;
  }


  std::cout << std::endl;
  return EXIT_SUCCESS;
}
